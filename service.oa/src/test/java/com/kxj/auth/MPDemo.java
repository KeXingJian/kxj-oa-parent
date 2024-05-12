package com.kxj.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.kxj.auth.mapper.SysRoleMapper;
import com.kxj.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class MPDemo {
    @Resource
    private SysRoleMapper sysRoleMapper;
    @Test
    public void getAll(){
        List<SysRole> list = sysRoleMapper.selectList(null);
        System.out.println(list);
    }

    @Test
    public void all(){
        for (int i=2;i<=100;i++){
            SysRole sysRole = new SysRole();
            sysRole.setRoleName(i+"man");
            sysRole.setRoleCode(i+"00");
            sysRole.setId((long) i);
            sysRole.setDescription("测试人员");
            int insert = sysRoleMapper.insert(sysRole);
            System.out.println(insert);
        }
    }
    @Test
    public void delete(){
        for (int i=2;i<=100;i++){
            int i1 = sysRoleMapper.deleteById(i);
            System.out.println(i);
        }
    }
    @Test
    public void demo01(){
        Map<String,Object> map=new HashMap<>();
        map.put("role_name","zs");
        int i = sysRoleMapper.deleteByMap(map);
        System.out.println(i);
    }
    @Test
    public void demo02(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("id");
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        System.out.println(list);
    }
    @Test
    public void demo03(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.like("description","测")
                .gt("id",50)
                .or()
                .ge("id",20);
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        list.forEach(System.out::println);
    }
    @Test
    public void demo04(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.inSql("id","select id from sys_role where id <=3");
        List<SysRole> list1 = sysRoleMapper.selectList(wrapper);
        list1.forEach(System.out::println);
        UpdateWrapper<SysRole> updateWrapper=new UpdateWrapper<>();
        updateWrapper
                .set("role_name","李四")
                .set("role_code","911")
                .set("description","码农")
                .like("role_name","z")
                .and(w->w.eq("role_code",100).or().isNotNull("description"));
        int update = sysRoleMapper.update(null, updateWrapper);
        System.out.println(update);
        List<SysRole> list2 = sysRoleMapper.selectList(wrapper);
        list2.forEach(System.out::println);
    }
    @Test
    public void demo05(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        String roleName=null;
        String roleCode="34";
        String description="产品经理";
        wrapper.like(StringUtils.isNotBlank(roleName),"role_name","李")
                .ge(roleCode!=null,"role_code",roleCode);
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        list.forEach(System.out::println);
    }
    @Test
    public void demo06(){
        LambdaUpdateWrapper<SysRole> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper
                .set(SysRole::getRoleName,"超级管理员")
                .set(SysRole::getRoleCode,23788)
                .set(SysRole::getDescription,"内裤外穿的超人")
                .like(SysRole::getRoleName,"李");
        SysRole sysRole = new SysRole();
        int update = sysRoleMapper.update(sysRole, lambdaUpdateWrapper);
        System.out.println(update);
    }
    @Test
    public void demo07(){
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(SysRole::getRoleName,"超级");
        List<SysRole> list = sysRoleMapper.selectList(wrapper);
        list.forEach(System.out::println);
    }
}
