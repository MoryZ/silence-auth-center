package com.old.silence.auth.center.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.kaptcha.Producer;
import com.old.silence.auth.center.enums.CaptchaType;
import com.old.silence.auth.center.enums.RedisCacheEnum;
import com.old.silence.auth.center.util.Base64Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author moryzang
 */
@RestController
@RequestMapping("/api/v1")
public class CaptchaResource {

    @Value("${silence.auth.center.captcha-enabled:true}")
    private boolean captchaEnabled;

    @Value("${silence.auth.center.captcha-type:MATH}")
    private CaptchaType captchaType;

    private final Producer captchaProducer;

    private final Producer captchaProducerMath;

    private final ValueOperations<String, String> valueOperations;


    public CaptchaResource(Producer captchaProducer, Producer captchaProducerMath,
                           StringRedisTemplate stringRedisTemplate) {
        this.captchaProducer = captchaProducer;
        this.captchaProducerMath = captchaProducerMath;
        this.valueOperations = stringRedisTemplate.opsForValue();
    }

    /**
     * 生成验证码
     */
    @GetMapping("/captcha/image")
    public Map<String, Object> getCode() throws IOException {
        Map<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled) {
            return objectHashMap;
        }

        // 保存验证码信息
        String uuid = UUID.randomUUID().toString();
        String verifyKey = RedisCacheEnum.CAPTCHA_CODE_KEY.getCacheKey(uuid);

        String capStr, code = null;
        BufferedImage image = null;

        // 生成验证码
        if (CaptchaType.MATH.equals(captchaType)) {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        } else if (CaptchaType.CHAR.equals(captchaType)) {
            String capText = captchaProducer.createText();
            capStr = capText;
            code = capText;
            image = captchaProducer.createImage(capStr);
        }

        valueOperations.set(verifyKey, code, 2, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
        } catch (IOException e) {
            throw new IOException();
        }

        objectHashMap.put("uuid", uuid);
        objectHashMap.put("img", Base64Utils.encode(os.toByteArray()));
        return objectHashMap;
    }

}