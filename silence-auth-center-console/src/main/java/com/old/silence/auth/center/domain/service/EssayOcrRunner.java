package com.old.silence.auth.center.domain.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.ITessAPI;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @author moryzang
 */
public class EssayOcrRunner {

    public static class OcrLine {
        public String text;
        public int x;
        public int y;
        public int w;
        public int h;

        public OcrLine(String text, int x, int y, int w, int h) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }

    public static class PageResult {
        public int pageIndex;
        public List<OcrLine> lines;

        public PageResult(int pageIndex, List<OcrLine> lines) {
            this.pageIndex = pageIndex;
            this.lines = lines;
        }
    }

    public static void main(String[] args) throws Exception {

        String tessdataDir = "C:\\Users\\Administrator\\Desktop\\tessdata";
        String outDir = "C:\\Users\\Administrator\\Desktop\\out";
        List<String> imagePaths = Arrays.asList(
                "C:\\Users\\Administrator\\Desktop\\1.jpg",
                "C:\\Users\\Administrator\\Desktop\\2.jpg",
                "C:\\Users\\Administrator\\Desktop\\3.jpg"
        );


        // 入参示例： args[0]=tessdata 目录， args[1]=输出目录， args[2..]=1~5张图片路径

        // 确保输出目录存在
        Files.createDirectories(Path.of(outDir));

        // 初始化 Tesseract
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataDir);      // 指向包含 chi_sim.traineddata 的目录
        tesseract.setLanguage("chi_sim");        // 简体中文
        // PSM 可按文稿布局调整。SINGLE_BLOCK/ AUTO。这里设为 AUTO，兼容标题+正文
        tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_AUTO);

        // 如果需要：打开特定引擎模式（LSTM）
        tesseract.setOcrEngineMode(ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY);

        // 对每页做行级识别
        List<PageResult> pageResults = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            String imgPath = imagePaths.get(i);
            BufferedImage image = ImageIO.read(new File(imgPath));
            if (image == null) {
                System.out.println("无法读取图片: " + imgPath);
                continue;
            }

            List<Word> words = tesseract.getWords(image, ITessAPI.TessPageIteratorLevel.RIL_TEXTLINE);
            // 过滤空行、去首尾空白，按照 y 自上而下排序
            List<OcrLine> lines = words.stream()
                    .map(w -> new OcrLine(safeTrim(w.getText()), w.getBoundingBox().x, w.getBoundingBox().y,
                            w.getBoundingBox().width, w.getBoundingBox().height))
                    .filter(l -> !isNullOrBlank(l.text))
                    .sorted(Comparator.<OcrLine>comparingInt(l -> l.y).thenComparingInt(l -> l.x))
                    .collect(Collectors.toList());

            pageResults.add(new PageResult(i + 1, lines));
        }

        // 合并全部页的行
        List<OcrLine> allLines = new ArrayList<>();
        for (PageResult pr : pageResults) {
            allLines.addAll(pr.lines);
        }

        // 标题与正文
        String title = "";
        List<String> bodyLines = new ArrayList<>();
        if (!allLines.isEmpty()) {
            title = allLines.get(0).text;
            for (int i = 1; i < allLines.size(); i++) {
                bodyLines.add(allLines.get(i).text);
            }
        }

        String body = String.join("\n", bodyLines);

        // 导出 JSON
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("title", title);
        root.put("body", body);

        ArrayNode pagesNode = mapper.createArrayNode();
        for (PageResult pr : pageResults) {
            ObjectNode pageNode = mapper.createObjectNode();
            pageNode.put("pageIndex", pr.pageIndex);
            ArrayNode linesNode = mapper.createArrayNode();
            for (OcrLine line : pr.lines) {
                ObjectNode ln = mapper.createObjectNode();
                ln.put("text", line.text);
                ln.put("x", line.x);
                ln.put("y", line.y);
                ln.put("w", line.w);
                ln.put("h", line.h);
                linesNode.add(ln);
            }
            pageNode.set("lines", linesNode);
            pagesNode.add(pageNode);
        }
        root.set("pages", pagesNode);

        Path jsonOut = Path.of(outDir, "essay_ocr.json");
        mapper.writerWithDefaultPrettyPrinter().writeValue(jsonOut.toFile(), root);

        // 导出 TXT（标题 + 空行 + 正文）
        StringBuilder txt = new StringBuilder();
        if (!title.isEmpty()) {
            txt.append(title).append("\n\n");
        }
        txt.append(body);
        Path txtOut = Path.of(outDir, "essay_ocr.txt");
        Files.writeString(txtOut, txt.toString(), StandardCharsets.UTF_8);

        System.out.println("完成。JSON: " + jsonOut.toAbsolutePath());
        System.out.println("完成。TXT: " + txtOut.toAbsolutePath());
    }

    private static boolean isNullOrBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
