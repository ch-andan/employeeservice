package com.pws.admin.dto;

import com.pws.admin.entity.Permission;
import com.pws.admin.entity.Role;
import com.pws.admin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author Vinayak M
 * @Date 16/01/23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBasicDetailsDTO {


    private User user;

    private List<Role> roleList;


    private List<Permission> permissionList;
}
