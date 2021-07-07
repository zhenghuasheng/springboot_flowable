package com.flowable.controller;

import com.flowable.config.ResultVo;
import liquibase.pro.packaged.R;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author zhs
 * @Description
 * @createTime 2021/4/22 0022 10:38
 */
@Controller
@RequestMapping("/flowable")
public class FlowableController {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessEngine processEngine;
    @Autowired
    private HistoryService historyService;

    @PostConstruct
    public void init() {
//        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery().finished().list();
//        System.out.println(list);
    }


    @RequestMapping(value = "startLeaveProcess")
    @ResponseBody
    public String startLeaveProcess(String staffId) {
        HashMap<String, Object> map = new HashMap<>(4);
        map.put("leaveTask", staffId);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("Leave_zhs", map);
        StringBuilder sb = new StringBuilder();
        sb.append("创建请假流程 processId：" + processInstance.getId());
        List<Task> tasks = taskService.createTaskQuery()
                .taskAssignee(staffId)
                .orderByTaskCreateTime()
                .desc()
                .list();
        for (Task task : tasks) {
            sb.append("任务taskId:" + task.getId());
        }
        return sb.toString();
    }

    /**
     * .获取用户的任务
     *
     * @param userId 用户id
     */
    @ResponseBody
    @GetMapping("/getTasks/{userId}")
    public ResultVo<String> getTasks(@PathVariable String userId) {
        ResultVo<String> resultVo = new ResultVo<>();
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        resultVo.setData(tasks.toString());
        return resultVo;
    }

    /**
     * 驳回
     *
     * @param taskId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "rejectTask")
    public String rejectTask(String taskId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("checkResult", "驳回");
        taskService.complete(taskId, map);
        return "申请审核驳回~";
    }


    /**
     * 通过
     *
     * @param taskId
     * @return
     */
    @RequestMapping(value = "applyTask")
    @ResponseBody
    public String applyTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new RuntimeException("流程不存在");
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("checkResult", "通过");
        taskService.complete(taskId, map);
        return "申请审核通过~";
    }


    /**
     * 生成流程图
     *
     * @param httpServletResponse
     * @param processId
     * @throws Exception
     */
    @RequestMapping(value = "createProcessDiagramPic")
    public void createProcessDiagramPic(HttpServletResponse httpServletResponse, String processId) throws Exception {

        ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        if (pi == null) {
            return;
        }
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

        String InstanceId = task.getProcessInstanceId();
        List<Execution> executions = runtimeService
                .createExecutionQuery()
                .processInstanceId(InstanceId)
                .list();

        List<String> activityIds = new ArrayList<>();
        List<String> flows = new ArrayList<>();
        for (Execution exe : executions) {
            List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
            activityIds.addAll(ids);
        }
        /**
         * 生成流程图
         */
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(), engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0);
        OutputStream out = null;
        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            out = httpServletResponse.getOutputStream();
            while ((legth = in.read(buf)) != -1) {
                out.write(buf, 0, legth);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
