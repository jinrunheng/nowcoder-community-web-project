package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/data")
    @ResponseBody
    public String getData() {
        return alphaService.find();
    }

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello() {
        return "Hello Spring Boot";
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // request method
        System.out.println(request.getMethod());
        // request path
        System.out.println(request.getServletPath());

        // headers
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + " : " + value);
        }
        // parameter
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String parameterKey = entry.getKey();
            String[] parameterValues = entry.getValue();
            for (String parameterValue : parameterValues) {
                System.out.println(parameterKey + " : " + parameterValue);
            }

        }

        // response
        response.setContentType("text/html;charset-utf-8");
        // try with resources
        try (PrintWriter writer = response.getWriter()) {
            writer.write("<h1>Hello HTTP</h1>");
        }
    }

    // GET request
    // path: /students?page=1&limit=20
    // @RequestMapping(path = "/student",method = RequestMethod.GET)
    @GetMapping(path = "/students")
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "page", required = false, defaultValue = "1") int page,
            @RequestParam(name = "limit", required = false, defaultValue = "10") int limit) {
        System.out.println(page);
        System.out.println(limit);
        return "some students";
    }

    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudentById(@PathVariable("id") int id) {
        System.out.println(id);
        return "a student";
    }

    // POST request
    @PostMapping(path = "/student")
    @ResponseBody
    public String saveStudent(@RequestParam(name = "name") String name,
                              @RequestParam(name = "age") int age) {
        System.out.println(name);
        System.out.println(age);
        return "success";
    }

    // 响应HTML数据
    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
    public ModelAndView getTeacher() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name", "张三");
        modelAndView.addObject("age", 30);
        modelAndView.setViewName("/demo/view");
        return modelAndView;
    }

    @RequestMapping(path = "/school", method = RequestMethod.GET)
    public String getSchool(Model model) {
        model.addAttribute("name", "北京大学");
        model.addAttribute("age", 80);
        return "/demo/view";
    }

    // 响应 JSON（异步请求）
    @RequestMapping(path = "/emp", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getEmp() {
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "张三");
        emp.put("age", 23);
        emp.put("salary", 8000);
        return emp;
    }

    @RequestMapping(path = "/emps", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getEmps() {
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> emp1 = new HashMap<>();
        emp1.put("name", "张三");
        emp1.put("age", 23);
        emp1.put("salary", 8000);
        list.add(emp1);

        Map<String, Object> emp2 = new HashMap<>();
        emp2.put("name", "李四");
        emp2.put("age", 29);
        emp2.put("salary", 10000);
        list.add(emp2);
        return list;
    }
}
