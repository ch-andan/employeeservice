package com.pws.admin.service;

import com.pws.admin.dto.PermissionDTO;

import com.pws.admin.dto.SignUpDTO;
import com.pws.admin.dto.UpdatePasswordDTO;
import com.pws.admin.dto.UserBasicDetailsDTO;
import com.pws.admin.dto.UserRoleXrefDTO;
import com.pws.admin.entity.*;
import com.pws.admin.entity.Module;
import com.pws.admin.exception.config.PWSException;
import com.pws.admin.repository.ModuleRepository;
import com.pws.admin.repository.PermissionRepository;
import com.pws.admin.repository.RoleRepository;
import com.pws.admin.repository.SkillMasterRepository;
import com.pws.admin.repository.UserRepository;
import com.pws.admin.repository.UserRoleXrefRepository;
import com.pws.admin.utility.DateUtils;
import com.pws.admin.utility.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

/**
 * @Author Vinayak M
 * @Date 09/01/23
 */

@Service
public class AdminServiceImpl implements AdminService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ModuleRepository moduleRepository;

	@Autowired
	private UserRoleXrefRepository userRoleXrefRepository;

	@Autowired
	private PermissionRepository permissionRepository;
	
	@Autowired
	private SkillMasterRepository skillMasterRepository;
	
//	@Autowired
//	private JwtUtil jwtUtil;
//	
//	@Autowired
//	private AuthenticationManager authenticationManager;
	
//	@Autowired
//	private UserDetails userDetails;
	
	@Override
	public void UserSignUp(SignUpDTO signupDTO) throws PWSException {

		Optional<User> optionalUser = userRepository.findUserByEmail(signupDTO.getEmail());
		if (optionalUser.isPresent())
			throw new PWSException("User Already Exist with Email : " + signupDTO.getEmail());
		User user = new User();
		user.setDateOfBirth(DateUtils.getUtilDateFromString(signupDTO.getDateOfBirth()));
		user.setFirstName(signupDTO.getFirstName());
		user.setIsActive(true);
		user.setLastName(signupDTO.getLastName());
		user.setEmail(signupDTO.getEmail());
		user.setPhoneNumber(signupDTO.getPhoneNumber());
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		// Set new password
		user.setPassword(encoder.encode(signupDTO.getPassword()));

		userRepository.save(user);
	}

	@Override
	public void addRole(Role role) throws PWSException {
		role.setIsActive(true);
		roleRepository.save(role);
	}

	@Override
	public void updateRole(Role role) throws PWSException {
		roleRepository.save(role);
	}

	@Override
	public List<Role> fetchAllRole() throws PWSException {
		return roleRepository.findAll();
	}

	@Override
	public Optional<Role> fetchRoleById(Integer id) throws PWSException {
		return roleRepository.findById(id);
	}

	@Override
	public void deactivateOrActivateRoleById(Integer id, boolean flag) throws PWSException {
		Optional<Role> role = roleRepository.findById(id);
		if (role.isPresent()) {
			role.get().setIsActive(flag);
			roleRepository.save(role.get());
		}
	}

	@Override
	public void addModule(Module module) throws PWSException {
		moduleRepository.save(module);
	}

	@Override
	public void updateModule(Module module) throws PWSException {
		Optional<Module> optionalModule = moduleRepository.findById(module.getId());
		if (optionalModule.isPresent()) {
			moduleRepository.save(module);
		} else
			throw new PWSException("Module Doest Exist");

	}

	@Override
	public List<Module> fetchAllModule() throws PWSException {
		return moduleRepository.findAll();
	}

	@Override
	public Optional<Module> fetchModuleById(Integer id) throws PWSException {
		Optional<Module> optionalModule = moduleRepository.findById(id);
		if (optionalModule.isPresent()) {
			return optionalModule;
		} else
			throw new PWSException("Module Doest Exist");
	}

	@Override
	public void deactivateOrActivateModuleById(Integer id, boolean flag) throws PWSException {
		Optional<Module> optionalModule = moduleRepository.findById(id);
		Module module = optionalModule.get();
		if (optionalModule.isPresent()) {
			module.setIsActive(flag);
			moduleRepository.save(module);
		} else
			throw new PWSException("Module Doest Exist");
	}

	@Override
	public void saveOrUpdateUserXref(UserRoleXrefDTO userRoleXrefDTO) throws PWSException {
		Optional<UserRoleXref> optionalUserRoleXref = userRoleXrefRepository.findById(userRoleXrefDTO.getId());
		UserRoleXref userRoleXref = null;
		if (optionalUserRoleXref.isPresent()) {
			userRoleXref = optionalUserRoleXref.get();
		} else {
			userRoleXref = new UserRoleXref();
		}
		Optional<User> optionalUser = userRepository.findById(userRoleXrefDTO.getUserId());
		if (optionalUser.isPresent()) {
			userRoleXref.setUser(optionalUser.get());
		} else {
			throw new PWSException("User Doest Exist");
		}

		Optional<Role> optionalRole = roleRepository.findById(userRoleXrefDTO.getRoleId());
		if (optionalRole.isPresent()) {
			userRoleXref.setRole(optionalRole.get());
		} else {
			throw new PWSException("Role Doest Exist");
		}
		userRoleXref.setIsActive(userRoleXrefDTO.getIsActive());

		userRoleXrefRepository.save(userRoleXref);

	}

	@Override
	public void deactivateOrActivateAssignedRoleToUser(Integer id, Boolean flag) throws PWSException {
		Optional<UserRoleXref> optionalUserRoleXref = userRoleXrefRepository.findById(id);
		UserRoleXref userRoleXref = optionalUserRoleXref.get();
		if (optionalUserRoleXref.isPresent()) {
			userRoleXref.setIsActive(flag);
			userRoleXrefRepository.save(userRoleXref);
		} else
			throw new PWSException("Record Doest Exist");

	}

	@Override
	public List<User> fetchUserByRole(Integer roleId) throws PWSException {
		return userRoleXrefRepository.fetchAllUsersByRoleId(roleId);
	}

	@Override
	public Optional<UserRoleXref> fetchAllUserRoleXrefById(Integer id) {
		return userRoleXrefRepository.findById(id);
	}

	@Override
	public void addPermission(PermissionDTO permissionDTO) throws PWSException {
		Permission permission = new Permission();

		permission.setIsActive(permissionDTO.getIsActive());
		permission.setIsAdd(permissionDTO.getIsAdd());
		permission.setIsDelete(permissionDTO.getIsDelete());
		permission.setIsUpdate(permissionDTO.getIsUpdate());
		permission.setIsView(permissionDTO.getIsView());
		Optional<Module> moduleId = moduleRepository.findById(permissionDTO.getModuleId());
		Optional<Role> roleId = roleRepository.findById(permissionDTO.getRoleId());
		permission.setRole(roleId.get());
		permission.setModel(moduleId.get());
		permissionRepository.save(permission);
	}

	@Override
	public void updatePermission(PermissionDTO permissionDTO) throws PWSException {
		Optional<Permission> optionalPermission = permissionRepository.findById(permissionDTO.getId());
		if (optionalPermission.isPresent()) {
			optionalPermission.get().setIsActive(permissionDTO.getIsActive());
			optionalPermission.get().setIsAdd(permissionDTO.getIsAdd());
			optionalPermission.get().setIsDelete(permissionDTO.getIsDelete());
			optionalPermission.get().setIsUpdate(permissionDTO.getIsUpdate());
			optionalPermission.get().setIsView(permissionDTO.getIsView());
			Optional<Role> roleId = roleRepository.findById(permissionDTO.getRoleId());
			Optional<Module> moduleId = moduleRepository.findById(permissionDTO.getModuleId());
			optionalPermission.get().setRole(roleId.get());
			optionalPermission.get().setModel(moduleId.get());
			permissionRepository.save(optionalPermission.get());
		} else
			throw new PWSException(" updatePermission Not Allowed");

	}

	@Override
	public List<Permission> fetchAllPermission() throws PWSException {
		return permissionRepository.findAll();
	}

	@Override
	public Optional<Permission> fetchPermissionById(Integer id) throws PWSException {
		return permissionRepository.findById(id);
	}

	@Override
	public void deactivateOrActivatePermissionById(Integer id, Boolean flag) throws PWSException {
		Optional<Permission> optionalPermission = permissionRepository.findById(id);
		Permission permission = null;
		if (optionalPermission.isPresent()) {
			permission = optionalPermission.get();
			permission.setIsActive(flag);
			permissionRepository.save(permission);
		} else {
			throw new PWSException("Id Not ");
		}
	}

	@Override
	public void updateUserPassword(UpdatePasswordDTO userPasswordDTO) throws PWSException {
		Optional<User> optionalUser = userRepository.findUserByEmail(userPasswordDTO.getUserEmail());

		PasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = null;
		if (!optionalUser.isPresent()) {
			throw new PWSException("User Not present ");
		}
		user = optionalUser.get();
		if (encoder.matches(userPasswordDTO.getOldPassword(), user.getPassword())) {
			if (userPasswordDTO.getNewPassword().equals(userPasswordDTO.getConfirmNewPassword())) {
				
				user.setPassword(encoder.encode(userPasswordDTO.getConfirmNewPassword()));
				userRepository.save(user);
			}
			else {
				throw new PWSException("new password and confirm password doesnot match ");
			}
	

		}
		else
		{
			throw new PWSException("oldpassword not matched");
		}
		
}
	@Override
	public UserBasicDetailsDTO getUserBasicInfoAfterLoginSuccess(String email) throws PWSException {
	    Optional<User> optionalUser = userRepository.findUserByEmail(email);
	    if(! optionalUser.isPresent())
	        throw new PWSException("User Already Exist with Email : " + email);


	    User user = optionalUser.get();
	    UserBasicDetailsDTO userBasicDetailsDTO =new UserBasicDetailsDTO();
	    userBasicDetailsDTO.setUser(user);

	    List<Role> roleList = userRoleXrefRepository.findAllUserRoleByUserId(user.getId());
	    userBasicDetailsDTO.setRoleList(roleList);
	    List<Permission> permissionList =null;
	    if(roleList.size()>0)
	     permissionList = permissionRepository.getAllUserPermisonsByRoleId(roleList.get(0).getId());

	    userBasicDetailsDTO.setPermissionList(permissionList);
	    return userBasicDetailsDTO;
	}

	@Override
	public void addSkills(SkillMastertable skillMastertable) throws PWSException {
		skillMasterRepository.save(skillMastertable);
		
	}

	@Override
	public void updateSkills(SkillMastertable skillMastertable) throws PWSException {
		Optional<SkillMastertable> optionalSkill = skillMasterRepository.findById(skillMastertable.getId());
		if (optionalSkill.isPresent()) {
			skillMasterRepository.save(skillMastertable);
		} else
			throw new PWSException("Module Doest Exist");

	}
		
	

	@Override
	public List<SkillMastertable> fetchAllSkills() throws PWSException {
		return skillMasterRepository.findAll();
	}
	
	@Override
	public Optional<SkillMastertable> fetchAllSkillsByid(Integer id) throws PWSException {
		return skillMasterRepository.findById(id);
	}
	

	

	

	@Override
	public void updateSkillbyId(Integer id, boolean flag, String name) throws PWSException {
		Optional<SkillMastertable> skillbyid = skillMasterRepository.findById(id);
		if (skillbyid.isPresent()) {
			skillbyid.get().setIsActive(flag);
			skillbyid.get().setSkillname(name);
			skillMasterRepository.save(skillbyid.get());
		}
	}

	

		
	}


//	@Override
//	public Boolean logoutByemail(LogoutDTO logoutDTO) throws PWSException {
//		Optional<User> optionalUse = userRepository.findUserByEmail(logoutDTO.getEmail());
//		
//		User user = null;
//		if (!optionalUse.isPresent()) {
//			throw new PWSException("User Not present ");
//		}else
//		{
//			try {
//				
//				
//				authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword())
//						);
//			} catch (Exception ex) {
//				throw new PWSException("inavalid username/password");
//			}
//			
//			return jwtUtil.invalidateToken(user.getEmail());
//		}
//		
//		}
			
		
	

//optionalUse.get().setEmail(logoutDTO.getEmail());
//optionalUse.get().setPassword(logoutDTO.getPassword());
//@Override
//public Boolean logoutByemail(String email, UserDetails userdetails) throws PWSException {
//	Optional<User> optuser=userRepository.findUserByEmail(email);
//	User user=null;
//	if(!optuser.isPresent()) {
//		throw new PWSException("User not found with this email");
//	}else {
//		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword()));
//		try {
//			return jwtUtil.invalidateToken(user.getUsername(),user);
//		} catch (Exception e) {
//			
//			e.printStackTrace();
//		}
//
//	}
//	return null;
//}