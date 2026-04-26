package com.old.silence.auth.center.infrastructure.persistence.dao;

import java.math.BigInteger;

import com.old.silence.auth.center.domain.model.Role;
import com.old.silence.data.mybatis.projection.ProjectionMapperRepository;

public interface RoleDao extends ProjectionMapperRepository<Role, BigInteger> {

}