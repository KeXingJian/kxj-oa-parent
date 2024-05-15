package com.kxj.system.helper;

import com.kxj.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList){
        List<SysMenu> trees=new ArrayList<>();
        sysMenuList.forEach(sysMenu -> {
            //找到头节点才开始
            if (sysMenu.getParentId()==0){
                trees.add(findChildren(sysMenu, sysMenuList));
            }
        });
        return trees;
    }
    public static SysMenu findChildren(SysMenu sysMenu,List<SysMenu> treeNodes){
        sysMenu.setChildren(new ArrayList<>());
        treeNodes.forEach(it->{
            if (sysMenu.getId().equals(it.getParentId())){
                if (sysMenu.getChildren()==null){
                    sysMenu.setChildren(new ArrayList<>());
                }
                sysMenu.getChildren().add(findChildren(it,treeNodes));
            }
        });
        return sysMenu;
    }
}
