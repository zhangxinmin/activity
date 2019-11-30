package cn.itcast.activiti;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.junit.Test;

public class HelloWorld {
	/**
	 * 使用框架提供的自动建表（不提供配置文件）
	 */
	@Test
	public void test1() {
		// 创建一个流程引擎配置对象
		ProcessEngineConfiguration conf = ProcessEngineConfiguration
				.createStandaloneProcessEngineConfiguration();
		// 设置数据源信息
		conf.setJdbcDriver("com.mysql.jdbc.Driver");
		conf.setJdbcUrl("jdbc:mysql:///activity");
		conf.setJdbcUsername("root");
		conf.setJdbcPassword("root");
		// 设置自动建表
		conf.setDatabaseSchemaUpdate("true");

		// 创建一个流程引擎对象，在创建流程引擎对象过程中会自动建表
		ProcessEngine processEngine = conf.buildProcessEngine();
	}

	/**
	 * 使用框架提供的自动建表（提供配置文件）---可以从框架提供的例子程序中获取
	 */
	@Test
	public void test2() {
		String resource = "activiti-context.xml";// 配置文件名称
		String beanName = "processEngineConfiguration";// 配置id值
		ProcessEngineConfiguration conf = ProcessEngineConfiguration
				.createProcessEngineConfigurationFromResource(resource,
						beanName);
		ProcessEngine processEngine = conf.buildProcessEngine();
	}

	/**
	 * 使用框架提供的自动建表（使用配置文件）
	 */
	@Test
	public void test3() {
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	}

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/**
	 * 部署流程定义(操作数据表：act_re_deployment、act_re_procdef、act_ge_bytearray)
	 */
	@Test
	public void test4() {
		// 获得一个部署构建器对象，用于加载流程定义文件（test1.bpmn,test.png）完成流程定义的部署
		DeploymentBuilder builder = processEngine.getRepositoryService()
				.createDeployment();
		// 加载流程定义文件
		builder.addClasspathResource("test1.bpmn");
		builder.addClasspathResource("test1.png");
		// 部署流程定义
		Deployment deployment = builder.deploy();
		System.out.println(deployment.getId());
	}

	/**
	 * 查询流程定义列表
	 */
	@Test
	public void test5() {
		/*
		 * processEngine.getRepositoryService().createDeploymentQuery().list();
		 * processEngine
		 * .getRuntimeService().createProcessInstanceQuery().list();
		 * processEngine.getTaskService().createTaskQuery().list();
		 */

		// 流程定义查询对象,用于查询表act_re_procdef
		ProcessDefinitionQuery query = processEngine.getRepositoryService()
				.createProcessDefinitionQuery();
		// 添加过滤条件
		query.processDefinitionKey("bxlc");
		// 添加排序条件
		query.orderByProcessDefinitionVersion().desc();
		// 添加分页查询
		query.listPage(0, 10);
		List<ProcessDefinition> list = query.list();
		for (ProcessDefinition pd : list) {
			System.out.println(pd.getId());
		}
	}

	/**
	 * 根据流程定义的id启动一个流程实例
	 */
	@Test
	public void test6() {
		String processDefinitionId = "bxlc:2:104";
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceById(processDefinitionId);
		System.out.println(processInstance.getId());
	}
	
	/**
	 * 查询任务列表
	 */
	@Test
	public void test7() {
		TaskQuery query = processEngine.getTaskService().createTaskQuery();
		String assignee = "张三";
		query.taskAssignee(assignee);
		List<Task> list = query.list();
		for (Task task : list) {
			System.out.println(task.getId() + " " + task.getName());
		}
	}
	
	/**
	 * 办理任务
	 */
	@Test
	public void test8(){
		String taskId= "402";
		processEngine.getTaskService().complete(taskId);
	}
}
