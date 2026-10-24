package com.campus.tlias.controller;

import com.campus.tlias.common.Result;
import com.campus.tlias.entity.Student;
import com.campus.tlias.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生接口层（Controller）。
 *
 * <p><b>对比项目二的 Main 类——这是最直观的升级：</b>
 * <table border="1">
 *   <tr><th></th><th>项目二 Main</th><th>这里 Controller</th></tr>
 *   <tr><td>入口</td><td>main() 方法</td><td>HTTP 请求 + @GetMapping/@PostMapping</td></tr>
 *   <tr><td>参数</td><td>从控制台读</td><td>@RequestBody / @RequestParam 自动绑定</td></tr>
 *   <tr><td>返回</td><td>System.out.println</td><td>返回对象 → 自动转 JSON</td></tr>
 * </table>
 *
 * <p>@RestController = @Controller + @ResponseBody
 * 意思是"这个类的每个方法，返回值直接序列化成 JSON 给前端，不找页面模板"。
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /** 查询全部学生：GET /student/list */
    @GetMapping("/list")
    public Result<List<Student>> list() {
        return Result.success(studentService.list());
    }

    /** 按 id 查询：GET /student/{id} */
    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Integer id) {
        return Result.success(studentService.getById(id));
    }

    /**
     * 新增学生：POST /student/add
     *
     * <p>@RequestBody：把前端发来的 JSON 自动转成 Student 对象。
     * @Valid：触发实体里的 @NotBlank 校验，失败会被 GlobalExceptionHandler 接住。
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody @Valid Student student) {
        studentService.add(student);
        return Result.success();
    }

    /** 修改学生：PUT /student/update */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody @Valid Student student) {
        studentService.update(student);
        return Result.success();
    }

    /** 删除学生：DELETE /student/{id} */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        studentService.delete(id);
        return Result.success();
    }
}
