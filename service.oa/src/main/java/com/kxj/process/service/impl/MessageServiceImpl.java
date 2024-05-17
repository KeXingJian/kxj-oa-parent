package com.kxj.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kxj.auth.service.SysUserService;
import com.kxj.model.process.Process;
import com.kxj.model.process.ProcessTemplate;
import com.kxj.model.system.SysUser;
import com.kxj.process.service.MessageService;
import com.kxj.process.service.ProcessService;
import com.kxj.process.service.ProcessTemplateService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Resource
    private ProcessService processService;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private ProcessTemplateService processTemplateService;

    @Resource
    private WxMpService wxMpService;

    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        Process process = processService.getById(processId);
        SysUser user = sysUserService.getById(userId);
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser submitSysUser = sysUserService.getById(process.getUserId());
        String openId = user.getOpenId();
        if (StringUtils.isEmpty(openId)) openId="oRd0961szP_HF1Dw0rD8J9Rc4pi8";

        WxMpTemplateMessage  templateMessage = WxMpTemplateMessage.builder()
                .toUser(openId)
                .templateId("3MgAq-3CTA5Fp4YUIEfA5re1yA6tjlPNlCiJ67OEFSE")
                .url("http://kxj1.v1.idcfengye.com/#/show/" + processId + "/" + taskId)
                .build();

        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry:formShowData.entrySet()){
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n");
        }

        templateMessage.addData(new WxMpTemplateData("first",
                submitSysUser.getName()+"提交"+processTemplate.getName()+
        ",请注意查看","#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1",process.getProcessCode(),"#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2",new DateTime(process.getCreateTime()).toString(),"#272727"));
        templateMessage.addData(new WxMpTemplateData("content",content.toString(),"#272727"));

        try {
            wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }
}
