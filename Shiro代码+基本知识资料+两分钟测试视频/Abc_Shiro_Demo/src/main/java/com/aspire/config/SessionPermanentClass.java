package com.aspire.config;

import java.io.Serializable;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.ValidatingSession;
import org.apache.shiro.session.mgt.eis.EnterpriseCacheSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;

import com.aspire.mapper.SessionCRUDMapper;
import com.aspire.util.SerializableUtils;

/**
 * session增删改查
 *
 * @author JustryDeng
 * @Date 2018年8月24日 下午5:44:19
 */
public class SessionPermanentClass extends EnterpriseCacheSessionDAO {

	@Autowired
	private SessionCRUDMapper sessionCRUDMapper;

	@Override
	protected Serializable doCreate(Session session) {
		Serializable jsessionId = generateSessionId(session);
		assignSessionId(session, jsessionId);
		// 增
		sessionCRUDMapper.doCreate((String) jsessionId, SerializableUtils.serialize(session));
		return jsessionId;
	}

	@Override
	protected Session doReadSession(Serializable jsessionId) {
		// 查
		String sessionString = sessionCRUDMapper.doReadSession((String) jsessionId);
		if (sessionString == null) {
			return null;
		}
		return SerializableUtils.deserialize(sessionString);
	}

	@Override
	protected void doUpdate(Session session) {
		if (session instanceof ValidatingSession && !((ValidatingSession) session).isValid()) {
			return;
		}
		Serializable jsessionId = generateSessionId(session);
		if (jsessionId != null) {
			// 改
			sessionCRUDMapper.doUpdate(SerializableUtils.serialize(session), (String) jsessionId);
		}
	}

	@Override
	protected void doDelete(Session session) {
		Serializable jsessionId = generateSessionId(session);
		if (jsessionId != null) {
			// 删
			sessionCRUDMapper.doDelete((String) jsessionId);
		}
	}
}
