/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portlet.messageboards.util;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceTestUtil;
import com.liferay.portal.util.TestPropsValues;
import com.liferay.portal.util.UserTestUtil;
import com.liferay.portlet.messageboards.model.MBBan;
import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.model.MBCategoryConstants;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBMessageConstants;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.model.MBThreadFlag;
import com.liferay.portlet.messageboards.service.MBBanLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBCategoryServiceUtil;
import com.liferay.portlet.messageboards.service.MBMessageLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBThreadFlagLocalServiceUtil;

import java.io.InputStream;

import java.util.Collections;
import java.util.List;

/**
 * @author Eudaldo Alonso
 * @author Daniel Kocsis
 */
public class MBTestUtil {

	public static MBBan addBan(long groupId) throws Exception {
		User user = UserTestUtil.addUser(
			"BannedUser", TestPropsValues.getGroupId());

		return addBan(groupId, user.getUserId());
	}

	public static MBBan addBan(long groupId, long banUserId) throws Exception {
		return addBan(TestPropsValues.getUserId(), groupId, banUserId);
	}

	public static MBBan addBan(long userId, long groupId, long banUserId)
		throws Exception {

		ServiceContext serviceContext = ServiceTestUtil.getServiceContext(
			groupId);

		return MBBanLocalServiceUtil.addBan(userId, banUserId, serviceContext);
	}

	public static MBCategory addCategory(long groupId) throws Exception {
		return addCategory(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
	}

	public static MBCategory addCategory(long groupId, long parentCategoryId)
		throws Exception {

		ServiceContext serviceContext = ServiceTestUtil.getServiceContext(
			groupId);

		return addCategory(
			ServiceTestUtil.randomString(), parentCategoryId, serviceContext);
	}

	public static MBCategory addCategory(ServiceContext serviceContext)
		throws Exception {

		return MBCategoryServiceUtil.addCategory(
			TestPropsValues.getUserId(),
			MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID,
			ServiceTestUtil.randomString(), StringPool.BLANK, serviceContext);
	}

	public static MBCategory addCategory(
			String name, long parentCategoryId, ServiceContext serviceContext)
		throws Exception {

		return MBCategoryServiceUtil.addCategory(
			TestPropsValues.getUserId(), parentCategoryId, name,
			ServiceTestUtil.randomString(), serviceContext);
	}

	public static MBMessage addMessage(long groupId) throws Exception {
		return addMessage(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID);
	}

	public static MBMessage addMessage(long groupId, long categoryId)
		throws Exception {

		ServiceContext serviceContext = ServiceTestUtil.getServiceContext(
			groupId);

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			Collections.emptyList();

		return MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), ServiceTestUtil.randomString(),
			groupId, categoryId, 0, 0, ServiceTestUtil.randomString(),
			ServiceTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			inputStreamOVPs, false, 0.0, false, serviceContext);
	}

	public static MBMessage addMessage(
			long categoryId, String keywords, boolean approved,
			ServiceContext serviceContext)
		throws Exception {

		MBMessage message = MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), ServiceTestUtil.randomString(),
			categoryId, keywords, keywords, serviceContext);

		if (!approved) {
			message = MBMessageLocalServiceUtil.updateStatus(
				message.getStatusByUserId(), message.getMessageId(),
				WorkflowConstants.STATUS_DRAFT, serviceContext);
		}

		return message;
	}

	public static MBMessage addMessageWithWorkflow(
			long groupId, boolean approved)
		throws Exception {

		return addMessageWithWorkflow(
			groupId, MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID, approved);
	}

	public static MBMessage addMessageWithWorkflow(
			long groupId, long categoryId, boolean approved)
		throws Exception {

		ServiceContext serviceContext = ServiceTestUtil.getServiceContext(
			groupId);

		return addMessage(groupId, categoryId, true, approved, serviceContext);
	}

	public static MBThreadFlag addThreadFlag(long groupId, MBThread thread)
		throws Exception {

		ServiceContext serviceContext = ServiceTestUtil.getServiceContext(
			groupId);

		MBThreadFlagLocalServiceUtil.addThreadFlag(
			TestPropsValues.getUserId(), thread, serviceContext);

		return MBThreadFlagLocalServiceUtil.getThreadFlag(
			TestPropsValues.getUserId(), thread);
	}

	protected static MBMessage addMessage(
			long groupId, long categoryId, boolean workflowEnabled,
			boolean approved, ServiceContext serviceContext)
		throws Exception {

		if (workflowEnabled) {
			serviceContext.setWorkflowAction(
				WorkflowConstants.ACTION_SAVE_DRAFT);

			if (approved) {
				serviceContext.setWorkflowAction(
					WorkflowConstants.ACTION_PUBLISH);
			}
		}

		List<ObjectValuePair<String, InputStream>> inputStreamOVPs =
			Collections.emptyList();

		MBMessage message = MBMessageLocalServiceUtil.addMessage(
			TestPropsValues.getUserId(), ServiceTestUtil.randomString(),
			groupId, categoryId, 0, 0, ServiceTestUtil.randomString(),
			ServiceTestUtil.randomString(), MBMessageConstants.DEFAULT_FORMAT,
			inputStreamOVPs, false, 0.0, false, serviceContext);

		return MBMessageLocalServiceUtil.getMessage(message.getMessageId());
	}

}