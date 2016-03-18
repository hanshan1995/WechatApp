package feiyizhan.weixin.msg.handle.text.cmd;

import java.util.ArrayList;
import java.util.List;

import blade.kit.json.JSONArray;
import blade.kit.json.JSONObject;
import blade.kit.json.JSONValue;
import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import feiyizhan.weixin.UserSession;
import feiyizhan.weixin.msg.handle.text.CmdTextMessageHandle;
import feiyizhan.weixin.util.UserUtil;

/**
 * 显示群明细
 * @author Pluto Xu
 *
 */
public class ShowGroupDetailCmdProcess extends BaseCmdProcess {
	private static final Logger LOGGER = LoggerFactory.getLogger(ShowGroupDetailCmdProcess.class);

	public ShowGroupDetailCmdProcess(CmdTextMessageHandle handle) {
		super(handle);
		// TODO 自动生成的构造函数存根
	}

	@Override
	public String help() {
		// TODO 自动生成的方法存根
		StringBuilder sb = new StringBuilder();
		sb.append("显示当前群明细命令:\n");
		sb.append("--群聊天模式\n");
		sb.append("【显示明细】:显示明细  -- 显示当前群明细\n");
		return sb.toString();
	}

	@Override
	public boolean process(String fromUserID, String toUserID, String content, String currUserID) {
		// TODO 自动生成的方法存根
		String[] cmds = content.split("[ ]");
		String cmd=null;
		if(cmds.length==2){
			return false;
		}else if(cmds.length==1){
			if(!toUserID.startsWith("@@")) return false;
			cmd = cmds[0];
		}else{
			return false;
		}
		
		if(!isProcess(cmd)){
			return false;
		}
		
		if("显示明细".equals(cmd)){
			this.getHandle().getSession().webwxsendmsg(getGroupDetail(this.getHandle().getSession().getGroup(toUserID)),toUserID);
			return true;	
		}else{
			return false;
		}	
	}

	@Override
	public boolean isProcess(String cmd) {
		// TODO 自动生成的方法存根
		if("显示明细".equals(cmd)){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 获取群明细
	 * @return
	 */
	private String getGroupDetail(JSONObject group){
		StringBuilder sb = new StringBuilder();
		sb.append("当前群【");
		sb.append(UserUtil.getUserRemarkName(group));
		sb.append("】:\n");
		JSONArray memberList = UserUtil.getGroupMemberList(group);
		sb.append("总共有【");
		sb.append(memberList.size());
		sb.append("】个成员\n");
		List<String> unisexList = new ArrayList<String>();
		List<String> maleList = new ArrayList<String>();
		List<String> femaleList = new ArrayList<String>();
		List<String> contactList = new ArrayList<String>();
		for(JSONValue val:memberList){
			JSONObject member = val.asObject();
			if(UserUtil.isFound(this.getHandle().getSession().MemberList, member)){  //该用户在个人通讯录里
				contactList.add(UserUtil.getGroupUserRemarkName(member));
			}
			int sex = member.getInt("Sex", -1);
			switch (sex){
			case 0:{ //不男不女
				unisexList.add(UserUtil.getGroupUserRemarkName(member));
				break;
			}
			case 1:{ //男性
				maleList.add(UserUtil.getGroupUserRemarkName(member));
				break;
			}
			case 2:{ //女性
				femaleList.add(UserUtil.getGroupUserRemarkName(member));
				break;
			}
			default:{  //未识别的性别信息
				LOGGER.info("[*]未识别的性别信息"+member);
			}
			}
		}
		sb.append(" 其中有【"+ maleList.size()+"】位男性。\n");
		sb.append(" 有【"+ femaleList.size()+"】位女性。\n");
		sb.append(" 有【"+ unisexList.size()+"】位未设置性别。\n");
		sb.append("分别是："+unisexList+"\n");
		sb.append(" 有【"+ contactList.size()+"】位已加为好友。\n");
		return sb.toString();
		
		
	}

}
