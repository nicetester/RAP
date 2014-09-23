package com.taobao.rigel.rap.tester.web.action;

import com.google.gson.Gson;
import com.taobao.rigel.rap.account.bo.User;
import com.taobao.rigel.rap.common.ActionBase;
import com.taobao.rigel.rap.common.HTTPUtils;
import com.taobao.rigel.rap.common.SystemVisitorLog;
import com.taobao.rigel.rap.project.bo.Page;
import com.taobao.rigel.rap.project.service.ProjectMgr;
import com.taobao.rigel.rap.tester.bo.SSOUserRes;

import java.util.List;
import java.util.Map;


public class TesterAction extends ActionBase {

	private static final long serialVersionUID = 1L;
	private ProjectMgr projectMgr;
	private int id;
	private Page page;
	private int projectId;

	public int getProjectId() {
		return projectId;
	}

	public Page getPage() {
		return page;
	}

	public ProjectMgr getProjectMgr() {
		return projectMgr;
	}

	public void setProjectMgr(ProjectMgr projectMgr) {
		this.projectMgr = projectMgr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String pageTester() {
		page = projectMgr.getPage(id);
		projectMgr.loadParamIdListForPage(page);
		projectId = page.getModule().getProject().getId();
		return SUCCESS;
	}
	
	/**
	 * used for system configuration when new version deployed
	 * @return
	 */
	public String ___init___() throws Exception {
        List<User> list = getAccountMgr().getUserList();
        int count = 1000;
        for (User u : list) {
            if (count-- <=0) {
                break;
            }
            String account = u.getAccount();
            String res = HTTPUtils.sendGet("");
            Gson gson  = new Gson();
            SSOUserRes json = gson.fromJson(res, SSOUserRes.class);
            if (json.content == null)
                System.out.println("not Ali employee");
            else {
                System.out.println("empId:" + json.content.empId);
                if (json.content.nickNameCn != null && !json.content.nickNameCn.isEmpty()) {
                    u.setName(json.content.nickNameCn);
                } else {
                    u.setName(json.content.lastName);
                }
                u.setRealname(json.content.lastName);
                u.setEmpId(json.content.empId);
                getAccountMgr().updateUser(u);

            }
        }

		return SUCCESS;
	}
}
