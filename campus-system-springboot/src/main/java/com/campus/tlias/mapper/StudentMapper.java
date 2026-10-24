package com.campus.tlias.mapper;

import com.campus.tlias.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 学生 Mapper —— 数据访问层。
 *
 * <p><b>对比项目二的 StudentDao，这是最大的变化：</b>
 * <table border="1">
 *   <tr><th></th><th>项目二（纯 JDBC）</th><th>这里（MyBatis）</th></tr>
 *   <tr><td>连接</td><td>自己 DBUtil.getConn()</td><td>Spring 自动注入连接池</td></tr>
 *   <tr><td>设参</td><td>ps.setString(1, ...)</td><td>用 #{} 自动映射</td></tr>
 *   <tr><td>执行</td><td>ps.executeUpdate()</td><td>方法名决定操作类型</td></tr>
 *   <tr><td>关连接</td><td>finally 里 close</td><td>框架自动回收</td></tr>
 *   <tr><td>结果映射</td><td>while(rs.next()) 手工搬</td><td>自动映射成实体</td></tr>
 * </table>
 *
 * <p>注意：Mapper 接口<strong>不需要写实现类</strong>，MyBatis 会用动态代理
 * 在运行时生成实现对象并注册进 Spring 容器——这是 MyBatis 最核心的魔法。
 */
@Mapper
public interface StudentMapper {

    /**
     * 查询全部学生（注解方式写 SQL）。
     *
     * <p>#{} 是占位符，等价于 JDBC 的 ?，会自动防 SQL 注入。
     * 对比项目二：这里没有 PreparedStatement、没有 setString、没有 executeQuery。
     */
    @Select("SELECT id, name, major, phone FROM student ORDER BY id")
    List<Student> findAll();

    /**
     * 按 id 查询单个学生。
     */
    @Select("SELECT id, name, major, phone FROM student WHERE id = #{id}")
    Student findById(@Param("id") Integer id);

    /**
     * 按专业查询（演示动态条件的注解写法）。
     *
     * <p>@Param 让 XML/注解里能用名字引用参数，多参数时必须加。
     */
    @Select("SELECT id, name, major, phone FROM student WHERE major = #{major} ORDER BY id")
    List<Student> findByMajor(@Param("major") String major);

    /**
     * 新增学生，返回受影响行数。
     *
     * <p>@Insert 插入，@Options(useGeneratedKeys = true) 表示把数据库自增的主键回填到对象 id 属性。
     */
    @org.apache.ibatis.annotations.Insert(
            "INSERT INTO student(name, major, phone) VALUES(#{name}, #{major}, #{phone})")
    @org.apache.ibatis.annotations.Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Student student);

    /**
     * 修改学生。
     */
    @org.apache.ibatis.annotations.Update(
            "UPDATE student SET name = #{name}, major = #{major}, phone = #{phone} WHERE id = #{id}")
    int update(Student student);

    /**
     * 删除学生。
     */
    @org.apache.ibatis.annotations.Delete("DELETE FROM student WHERE id = #{id}")
    int deleteById(@Param("id") Integer id);
}
