package cn.itcast.activiti;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 * 使用Activiti框架的API操作流程
 * 
 * @author zhaoqx
 * 
 */
public class ActivitiAPITest {
	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	/**
	 * 部署流程定义 方式一：读取单个的流程定义文件 方式二：读取zip压缩文件
	 */
	@Test
	public void test1() {
		DeploymentBuilder deploymentBuilder = processEngine
				.getRepositoryService().createDeployment();

		// 方式一：读取单个的流程定义文件
		deploymentBuilder.addClasspathResource("test1.bpmn");
		deploymentBuilder.addClasspathResource("test1.png"); Deployment
		deployment = deploymentBuilder.deploy();

		// 方式二：读取zip压缩文件
		/*ZipInputStream zipInputStream = new ZipInputStream(this.getClass()
				.getClassLoader().getResourceAsStream("process.zip"));
		deploymentBuilder.addZipInputStream(zipInputStream);
		deploymentBuilder.name("请假流程部署");
		Deployment deployment = deploymentBuilder.deploy();*/
	}

	/**
	 * 查询部署列表
	 */
	@Test
	public void test2() {
		// 部署查询对象，查询表act_re_deployment
		DeploymentQuery query = processEngine.getRepositoryService()
				.createDeploymentQuery();
		List<Deployment> list = query.list();
		for (Deployment deployment : list) {
			String id = deployment.getId();
			System.out.println(id);
		}
	}

	/**
	 * 查询流程定义列表
	 */
	@Test
	public void test3() {
		// 流程定义查询对象，查询表act_re_procdef
		ProcessDefinitionQuery query = processEngine.getRepositoryService()
				.createProcessDefinitionQuery();
		List<ProcessDefinition> list = query.list();
		for (ProcessDefinition pd : list) {
			System.out.println(pd.getName() + "" + pd.getId());
		}
	}

	/**
	 * 删除部署信息
	 */
	@Test
	public void test4() {
		String deploymentId = "1001";
		// processEngine.getRepositoryService().deleteDeployment(deploymentId);
		processEngine.getRepositoryService().deleteDeployment(deploymentId,
				true);
	}

	/**
	 * 删除流程定义(通过删除部署信息达到删除流程定义的目的)
	 */
	@Test
	public void test5() {
		String deploymentId = "1401";
		// processEngine.getRepositoryService().deleteDeployment(deploymentId);
		processEngine.getRepositoryService().deleteDeployment(deploymentId,
				true);
	}

	/**
	 * 查询一次部署对应的流程定义文件名称和对应的输入流（bpmn png）
	 * 
	 * @throws Exception
	 */
	@Test
	public void test6() throws Exception {
		String deploymentId = "101";
		List<String> names = processEngine.getRepositoryService()
				.getDeploymentResourceNames(deploymentId);
		for (String name : names) {
			System.out.println(name);
			InputStream in = processEngine.getRepositoryService()
					.getResourceAsStream(deploymentId, name);
			// 将文件保存到本地磁盘
			/*
			 * OutputStream out = new FileOutputStream(new File("d:\\" + name));
			 * byte[] b = new byte[1024]; int len = 0; while((len =
			 * in.read(b))!=-1) { out.write(b, 0, len); } out.close();
			 */
			FileUtils.copyInputStreamToFile(in, new File("d:\\" + name));
			in.close();
		}
	}

	/**
	 * 获得png文件的输入流
	 * 
	 * @throws Exception
	 */
	@Test
	public void test7() throws Exception {
		String processDefinitionId = "qjlc:9:1204";
		InputStream pngInputStream = processEngine.getRepositoryService()
				.getProcessDiagram(processDefinitionId);
		FileUtils.copyInputStreamToFile(pngInputStream, new File("d:\\my.png"));
	}

	/**
	 * 启动流程实例 方式一：根据流程定义的id启动 方式二：根据流程定义的key启动(自动选择最新版本的流程定义启动流程实例)
	 */
	@Test
	public void test8() {
		/*
		 * String processDefinitionId = "qjlc:9:1204"; ProcessInstance
		 * processInstance =
		 * processEngine.getRuntimeService().startProcessInstanceById
		 * (processDefinitionId ); System.out.println(processInstance.getId());
		 */

		String processDefinitionKey = "qjlc";
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);
		System.out.println(processInstance.getId());
	}

	/**
	 * 查询流程实例列表,查询act_ru_execution表
	 */
	@Test
	public void test9(){
		//流程实例查询对象，查询act_ru_execution表
		ProcessInstanceQuery query = processEngine.getRuntimeService().createProcessInstanceQuery();
		query.processDefinitionKey("qjlc");
		query.orderByProcessInstanceId().desc();
		query.listPage(0, 2);
		List<ProcessInstance> list = query.list();
		for (ProcessInstance pi : list) {
			System.out.println(pi.getId() + " " + pi.getActivityId());
		}
	}

	/**
	 * 结束流程实例,操作的表act_ru_execution act_ru_task
	 */
	@Test
	public void test10(){
		String processInstanceId = "1601";
		processEngine.getRuntimeService().deleteProcessInstance(processInstanceId , "我愿意");
	}

	/**
	 * 查询任务列表
	 */
	@Test
	public void test11(){
		//任务查询对象,查询act_ru_task表
		TaskQuery query = processEngine.getTaskService().createTaskQuery();
		String assignee = "李四";
		query.taskAssignee(assignee);
		query.orderByTaskCreateTime().desc();
		List<Task> list = query.list();
		for (Task task : list) {
			System.out.println(task.getId());
		}
	}

	/**
	 * 办理任务
	 */
	@Test
	public void test12(){
		String taskId = "2902";
		processEngine.getTaskService().complete(taskId);
	}

	/**
	 * 直接将流程向下执行一步
	 */
	@Test
	public void test13(){
		String executionId = "2701";//流程实例id
		processEngine.getRuntimeService().signal(executionId);
	}
	
	/**
	 * 查询最新版本的流程定义列表
	 */
	@Test
	public void test14(){
		ProcessDefinitionQuery query = processEngine.getRepositoryService().createProcessDefinitionQuery();
		query.orderByProcessDefinitionVersion().asc();
		List<ProcessDefinition> list = query.list();
		Map<String, ProcessDefinition> map = new HashMap<String, ProcessDefinition>();
		for (ProcessDefinition pd : list) {
			map.put(pd.getKey(), pd);
		}
		ArrayList<ProcessDefinition> lastList = new ArrayList<>(map.values());
		for (ProcessDefinition processDefinition : lastList) {
			System.out.println(processDefinition.getName() + "  "+ processDefinition.getVersion() );
		}
	}
}
