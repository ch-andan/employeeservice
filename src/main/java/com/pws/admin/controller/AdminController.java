package com.pws.admin.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pws.admin.ApiSuccess;

import com.pws.admin.dto.PermissionDTO;
import com.pws.admin.dto.SignUpDTO;
import com.pws.admin.dto.UpdatePasswordDTO;
import com.pws.admin.dto.UserBasicDetailsDTO;
import com.pws.admin.dto.UserRoleXrefDTO;
import com.pws.admin.entity.Module;
import com.pws.admin.entity.Permission;
import com.pws.admin.entity.Role;
import com.pws.admin.entity.SkillMastertable;
import com.pws.admin.entity.User;
import com.pws.admin.entity.UserRoleXref;
import com.pws.admin.exception.config.PWSException;
import com.pws.admin.security.oauth2.config.JwtFilter;
import com.pws.admin.service.AdminService;
import com.pws.admin.utility.CommonUtils;
import com.pws.admin.utility.JwtUtil;
import com.pws.admin.entity.User;

/**
 * @Author Vinayak M
 * @Date 09/01/23
 */

@RestController
@RequestMapping("/")
public class AdminController {

	Logger log = LoggerFactory.getLogger(AdminController.class);

	@Autowired
	private AdminService adminService;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private AuthenticationManager authenticationManager;

	@PostMapping("public/signup")
	public ResponseEntity<Object> signup(@RequestBody SignUpDTO signUpDTO) throws PWSException {
		adminService.UserSignUp(signUpDTO);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.CREATED));
	}

	@PostMapping("/authenticate")
	public String generateToken(@RequestBody User user) throws Exception {
		log.info("authentication succesfull");
		try {

			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
		} catch (Exception ex) {
			throw new Exception("inavalid username/password");
		}
		return jwtUtil.generateToken(user.getUsername());
	}

	@PostMapping("private/add/role")
	public ResponseEntity<Object> addRole(@RequestBody Role role) throws PWSException {
		adminService.addRole(role);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PutMapping("private/update/role")
	public ResponseEntity<Object> updateRole(@RequestBody Role role) throws PWSException {
		adminService.updateRole(role);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PostMapping("private/fetch/role/by/id")
	public ResponseEntity<Object> fetchRoleById(@RequestParam Integer id) throws PWSException {
		Optional<Role> role = adminService.fetchRoleById(id);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, role.get()));
	}

	@GetMapping("private/fetch/all/role")
	public ResponseEntity<Object> fetchAllRole() throws PWSException {
		List<Role> roleList = adminService.fetchAllRole();
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, roleList));
	}

	@PostMapping("private/role/activate/deactivate")
	public ResponseEntity<Object> deactivateOrActivateRoleById(@RequestParam Integer id, @RequestParam Boolean flag)
			throws PWSException {
		adminService.deactivateOrActivateRoleById(id, flag);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PostMapping("private/add/module")
	public ResponseEntity<Object> addModule(@RequestBody Module module) throws PWSException {
		adminService.addModule(module);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PutMapping("private/update/module")
	public ResponseEntity<Object> updateModule(@RequestBody Module module) throws PWSException {
		adminService.updateModule(module);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@GetMapping("private/fetch/all/module")
	public ResponseEntity<Object> fetchAllModule() throws PWSException {
		List<Module> moduleList = adminService.fetchAllModule();
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, moduleList));
	}

	@GetMapping("private/fetch/all/module/by/id")
	public ResponseEntity<Object> fetchModuleById(@RequestParam Integer id) throws PWSException {
		Optional<Module> module = adminService.fetchModuleById(id);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, module.get()));
	}

	@PostMapping("private/module/activate/deactivate")
	public ResponseEntity<Object> deactivateOrActivateModuleById(@RequestParam Integer id, @RequestParam Boolean flag)
			throws PWSException {
		adminService.deactivateOrActivateModuleById(id, flag);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PostMapping("private/UserXref/save/Update")
	public ResponseEntity<Object> saveOrUpdateUserXref(@RequestBody UserRoleXrefDTO userRoleXrefDTO)
			throws PWSException {
		adminService.saveOrUpdateUserXref(userRoleXrefDTO);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PostMapping("private/UserXref/Activate/deactivate")
	public ResponseEntity<Object> deactivateOrActivateAssignedRoleToUser(@RequestParam Integer id,
			@RequestParam Boolean flag) throws PWSException {
		adminService.deactivateOrActivateAssignedRoleToUser(id, flag);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@GetMapping("private/fetch/all/userbyrole")
	public ResponseEntity<Object> fetchUserByRole(@RequestParam Integer roleId) throws PWSException {
		List<User> userRoleList = adminService.fetchUserByRole(roleId);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, userRoleList));
	}

	@GetMapping("private/fetch/userRoleXref/by/id")
	public ResponseEntity<Object> fetchAllUserRoleXrefById(@RequestParam Integer id) throws PWSException {
		Optional<UserRoleXref> userRoleXrefOptional = adminService.fetchAllUserRoleXrefById(id);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, userRoleXrefOptional));
	}

	@PostMapping("private/add/permission")
	public ResponseEntity<Object> addPermission(@RequestBody PermissionDTO permissionDTO) throws PWSException {
		adminService.addPermission(permissionDTO);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PutMapping("private/update/permission")
	public ResponseEntity<Object> updatePermission(@RequestBody PermissionDTO permissionDTO) throws PWSException {
		adminService.updatePermission(permissionDTO);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@GetMapping("private/fetch/all/permission")
	public ResponseEntity<Object> fetchAllPermission() throws PWSException {
		List<Permission> permissionList = adminService.fetchAllPermission();
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, permissionList));
	}

	@GetMapping("private/fetch/all/permission/by/id")
	public ResponseEntity<Object> fetchPermissionById(@RequestParam Integer id) throws PWSException {
		Optional<Permission> permission = adminService.fetchPermissionById(id);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, permission.get()));
	}

	@PostMapping("private/permission/activate/deactivate")
	public ResponseEntity<Object> deactivateOrActivatePermissionById(@RequestParam Integer id,
			@RequestParam Boolean flag) throws PWSException {
		adminService.deactivateOrActivatePermissionById(id, flag);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PutMapping("private/update/user/password")
	public ResponseEntity<Object> updateUserPassword(@RequestBody UpdatePasswordDTO userPasswordDTO)
			throws PWSException {
		adminService.updateUserPassword(userPasswordDTO);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@GetMapping("token/refresh")
	public String refreshToken(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String authorizationHeader = request.getHeader(AUTHORIZATION);
		String token = authorizationHeader.substring(7);
		Boolean isexp = jwtUtil.isTokenExpired(token);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && isexp != false) {
			return jwtUtil.refreshToken(token);
		} else {
			throw new PWSException("enter a expired valid token");
		}

	}

	@DeleteMapping("logout")
	public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
		String authHeader = request.getHeader(AUTHORIZATION);
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String jwttoken = authHeader.substring(7);
			jwtUtil.invalidateToken(jwttoken);
			request.getSession().removeAttribute("userDetails");
			return ResponseEntity.ok("Successfully logged out.");
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token.");
		}
	}

	@GetMapping("private/fetch/user/basic/info")
	public ResponseEntity<Object> getUserBasicInfoAfterLoginSuccess(@RequestParam String email) throws PWSException {
		UserBasicDetailsDTO userBasicDetailsDTO = adminService.getUserBasicInfoAfterLoginSuccess(email);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, userBasicDetailsDTO));
	}

	@PostMapping("private/add/skills")
	public ResponseEntity<Object> addSkills(@RequestBody SkillMastertable skillMastertable) throws PWSException {
		adminService.addSkills(skillMastertable);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	@PutMapping("private/update/skills")
	public ResponseEntity<Object> updateSkills(@RequestBody SkillMastertable skillMastertable) throws PWSException {
		adminService.updateSkills(skillMastertable);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}
	@GetMapping("private/fetch/all/skills")
	public ResponseEntity<Object> fetchAllSkills() throws Exception {
		List<SkillMastertable> skilllist = adminService.fetchAllSkills();
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, skilllist));
	}
	@GetMapping("private/fetch/all/skills")
	public ResponseEntity<Object> fetchAllSkillsbyid(@RequestParam Integer id) throws Exception {
		Optional<SkillMastertable> skilllist = adminService.fetchAllSkillsByid(id);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK, skilllist));
	}
	
	@PostMapping("private/skiill/update/id")
	public ResponseEntity<Object> updateSkillbyId(@RequestParam Integer id,Boolean flag,String name)
			throws PWSException {
		adminService.updateSkillbyId(id, flag,name);
		return CommonUtils.buildResponseEntity(new ApiSuccess(HttpStatus.OK));
	}

	

	
}
