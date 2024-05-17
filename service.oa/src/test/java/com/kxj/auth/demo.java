package com.kxj.auth;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class demo {
    @Resource
    private RepositoryService repositoryService;
    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;

    @Test
    public void suspend(){
        ProcessDefinition qingjia = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey("qingjia").singleResult();

        boolean suspended = qingjia.isSuspended();
        if (suspended){
            repositoryService.activateProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println("激活");
        }else {
            repositoryService.suspendProcessDefinitionById(qingjia.getId(),true,null);
            System.out.println("挂起");
        }
    }

    @Test
    public void add(){
        ProcessInstance qingjia = runtimeService.startProcessInstanceByKey("qingjia", "1001");
        System.out.println(qingjia.getBusinessKey());
    }

    @Test
    public void history(){
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee("zhangsan")
                .finished().list();
        list.forEach(historicTaskInstance -> {
            System.out.println(historicTaskInstance.getId());
            System.out.println(historicTaskInstance.getName());
        });
    }

    @Test
    public void completeTask(){
        Task task = taskService
                .createTaskQuery()
                .taskAssignee("zhangsan")
                .singleResult();
        taskService.complete(task.getId());
    }
    @Test
    public void find(){
        List<Task> list = taskService
                .createTaskQuery()
                .taskAssignee("zhangsan")
                .list();
        list.forEach(task -> {
            System.out.println(task.getId());
            System.out.println(task.getName());
        });

    }
    @Test
    public void test01(){
        Deployment deploy = repositoryService.createDeployment()
                .addClasspathResource("process/qingjia.bpmn20.xml")
                .addClasspathResource("process/qingjia.png")
                .name("请假申请流程")
                .deploy();
        System.out.println(deploy.getId());
        System.out.println(deploy.getName());
    }
    @Test
    public void startProcess(){
        ProcessInstance qingjia = runtimeService.startProcessInstanceByKey("qingjia");
        System.out.println(qingjia.getProcessDefinitionId());
        System.out.println(qingjia.getId());
        System.out.println(qingjia.getActivityId());
    }
}
