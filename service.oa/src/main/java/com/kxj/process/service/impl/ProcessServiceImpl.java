package com.kxj.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kxj.auth.service.SysUserService;
import com.kxj.model.process.Process;
import com.kxj.model.process.ProcessRecord;
import com.kxj.model.process.ProcessTemplate;
import com.kxj.model.system.SysUser;
import com.kxj.process.mapper.ProcessMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kxj.process.service.MessageService;
import com.kxj.process.service.ProcessRecordService;
import com.kxj.process.service.ProcessService;
import com.kxj.process.service.ProcessTemplateService;
import com.kxj.security.custom.LoginUserInfoHelper;
import com.kxj.vo.process.ApprovalVo;
import com.kxj.vo.process.ProcessFormVo;
import com.kxj.vo.process.ProcessQueryVo;
import com.kxj.vo.process.ProcessVo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.Event;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author kxj
 * @since 2024-05-16
 */
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Resource
    private RepositoryService repositoryService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ProcessTemplateService processTemplateService;
    @Resource
    private RuntimeService runtimeService;
    @Resource
    private TaskService taskService;

    @Resource
    private HistoryService historyService;

    @Resource
    private ProcessRecordService processRecordService;

    @Resource
    private MessageService messageService;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        IPage<ProcessVo> pageModel=
                baseMapper.selectPage(pageParam,processQueryVo);
        return pageModel;
    }

    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream =
                this.getClass().getClassLoader().getResourceAsStream(deployPath);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        //部署
        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .name("请假申请流程")
                .deploy();
        System.out.println(deployment.getId());
        System.out.println(deployment.getName());
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) {
        SysUser user = sysUserService.getById(LoginUserInfoHelper.getUserId());
        ProcessTemplate processTemplate = processTemplateService
                .getById(processFormVo.getProcessTemplateId());

        Process process = new Process();
        BeanUtils.copyProperties(processFormVo,process);
        process.setStatus(1);
        String workNo=System.currentTimeMillis()+"";
        process.setProcessCode(workNo);
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setFormValues(processFormVo.getFormValues());
        process.setTitle(user.getName()+"发起"+processTemplate.getName()+"申请");
        baseMapper.insert(process);

        String processDefinitionKey = processTemplate.getProcessDefinitionKey();
        String businessKey = String.valueOf(process.getId());

        String fomValues =processFormVo.getFormValues();
        JSONObject jsonObject = JSON.parseObject(fomValues);
        JSONObject formData = jsonObject.getJSONObject("formData");
        Map<String, Object> map = new HashMap<>(formData);
        Map<String,Object> variable=new HashMap<>();
        variable.put("data",map);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey(
                        processDefinitionKey,
                        businessKey,
                        variable);
        List<Task> taskList=this.getCurrentTaskList(processInstance.getId());
        System.out.println(taskList);

        List<String> nameList=new ArrayList<>();
        taskList.forEach(task -> {
            String assigneeName = task.getAssignee();
            SysUser taskUser = sysUserService.getUserByUsername(assigneeName);
            String name = taskUser.getName();
            nameList.add(name);
            messageService.pushPendingMessage(process.getId(),user.getId(),task.getId());

        });
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待"+ StringUtils.join(nameList.toArray(),",")+"审批");
        baseMapper.updateById(process);
        processRecordService.record(process.getId(),1,"发起申请");
    }

    @Override
    public IPage<ProcessVo> findPending(Page<Process> pageParam) {
        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();

        int begin=(int) ((pageParam.getCurrent()-1)*pageParam.getSize());
        int size =(int) pageParam.getSize();

        List<Task> tasks = query.listPage(begin, size);
        long count = query.count();

        List<ProcessVo> processVoList=new ArrayList<>();
        for(Task task:tasks){
            String processInstanceId = task.getProcessInstanceId();
            ProcessInstance processInstance =
                    runtimeService
                            .createProcessInstanceQuery()
                            .processInstanceId(processInstanceId)
                            .singleResult();
            String businessKey = processInstance.getBusinessKey();
            if (businessKey==null) continue;
            long processId = Long.parseLong(businessKey);
            Process process = baseMapper.selectById(processId);
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId(task.getId());
            processVoList.add(processVo);
        }
        Page<ProcessVo> page = new Page<>(pageParam.getCurrent(),
                pageParam.getSize(), count);
        page.setRecords(processVoList);
        return page;
    }

    @Override
    public Map<String, Object> show(Long id) {
        Process process=baseMapper.selectById(id);

        List<ProcessRecord> list = processRecordService
                .list(new LambdaQueryWrapper<ProcessRecord>()
                        .eq(ProcessRecord::getProcessId, id));

        ProcessTemplate processTemplate = processTemplateService
                .getById(process.getProcessTemplateId());
        boolean isApprove=false;
        List<Task> taskList = this.getCurrentTaskList(process
                .getProcessInstanceId());
        for(Task task : taskList) {
            //判断任务审批人是否是当前用户
            String username = LoginUserInfoHelper.getUsername();
            if(task.getAssignee().equals(username)) {
                isApprove = true;
            }
        }

        Map<String,Object> map=new HashMap<>();
        map.put("process",process);
        map.put("processRecordList",list);
        map.put("processTemplate",processTemplate);
        map.put("isApprove",isApprove);
        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) {
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(entry.getValue());
        }
        if (approvalVo.getStatus() == 1) {
            Map<String, Object> variable = new HashMap<>();
            taskService.complete(taskId, variable);
        } else {
            this.endTask(taskId);
        }
        String description = approvalVo.getStatus() == 1 ? "已通过" : "驳回";
        processRecordService.record(approvalVo.getProcessId(),
                approvalVo.getStatus(),
                description);

        Process process = baseMapper.selectById(approvalVo.getProcessId());

        List<Task> taskList=this.
                getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(taskList)){
            List<String> assignList=new ArrayList<>();
            taskList.forEach(task -> {
                String assignee = task.getAssignee();
                SysUser user = sysUserService.getUserByUsername(assignee);
                assignList.add(user.getName());
            });
            process.setDescription("等待"+StringUtils.join(assignList.toArray(),"")+"审批");
            process.setStatus(1);
        }else {
            if (approvalVo.getStatus() ==1){
                process.setDescription("审批完成(通过)");
                process.setStatus(2);
            }else {
                process.setDescription("审批完成(驳回)");
                process.setStatus(-1);
            }
            baseMapper.updateById(process);
        }
    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<java.lang.Process> pageParam) {
        HistoricTaskInstanceQuery query = historyService
                .createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished().orderByTaskCreateTime().desc();

        int begin=(int)((pageParam.getCurrent()-1)*pageParam.getSize());
        int size=(int) pageParam.getSize();
        List<HistoricTaskInstance> list = query.listPage(begin,size);
        long count = query.count();

        List<ProcessVo> processVoList=new ArrayList<>();

        list.forEach(item->{
            String processInstanceId = item.getProcessInstanceId();
            Process process = baseMapper
                    .selectOne(new LambdaQueryWrapper<Process>()
                            .eq(Process::getProcessInstanceId, processInstanceId));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVo.setTaskId("0");
            processVoList.add(processVo);
        });
        IPage<ProcessVo> pageModel=
                new Page<>(pageParam.getCurrent(),
                        pageParam.getSize(),count);

        pageModel.setRecords(processVoList);
        return pageModel;
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam,
                processQueryVo);
        pageModel.getRecords().forEach(item->{
            item.setTaskId("0");
        });
        return pageModel;
    }

    private void endTask(String taskId) {
        Task task = taskService.createTaskQuery()
                .taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService
                .getBpmnModel(task.getProcessDefinitionId());
        List<Event> endEvenList = bpmnModel
                .getMainProcess()
                .findFlowElementsOfType(Event.class);
        if (CollectionUtils.isEmpty(endEvenList)) return;
        FlowNode endFlowNode= endEvenList.get(0);
        FlowNode currentFlowNode=(FlowNode) bpmnModel
                .getMainProcess()
                .getFlowElement(task.getTaskDefinitionKey());

        List<SequenceFlow> originalSequenceFlowList = new ArrayList<>(currentFlowNode.getOutgoingFlows());
        currentFlowNode.getOutgoingFlows().clear();

        SequenceFlow newSequenceFlow=new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);

        List<SequenceFlow> newSequenceFlowList=new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        taskService.complete(task.getId());
    }


    private List<Task> getCurrentTaskList(String id) {
        return taskService.createTaskQuery()
                .processInstanceId(id).list();
    }


}
