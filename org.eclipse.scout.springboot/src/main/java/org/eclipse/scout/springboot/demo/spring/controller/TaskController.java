package org.eclipse.scout.springboot.demo.spring.controller;

import java.util.Collection;
import java.util.UUID;

import org.eclipse.scout.springboot.demo.model.Task;
import org.eclipse.scout.springboot.demo.spring.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <h3>{@link TaskController}</h3>
 *
 * @author patbaumgartner
 */
@Controller
@RequestMapping("/tasks")
public class TaskController {

  @Autowired
  private TaskService taskService;

  @RequestMapping("/")
  public @ResponseBody Collection<Task> showTasks() {
    return taskService.getAllTasks();
  }

  @RequestMapping("/{id}")
  public @ResponseBody Task showTaskById(@PathVariable String id) {
    return taskService.getTask(UUID.fromString(id));
  }

}