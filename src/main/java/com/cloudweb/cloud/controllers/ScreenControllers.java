package com.cloudweb.cloud.controllers;

import com.cloudweb.cloud.authentication.user.User;
import com.cloudweb.cloud.authentication.user.UserRepository;
import com.cloudweb.cloud.database.FileStorageService;
import com.cloudweb.cloud.database.FilesRenderService;
import com.cloudweb.cloud.database.UserStorageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/control")
public class ScreenControllers {

	@Autowired
	private FilesRenderService filesRenderService;

	@Autowired
	private FileStorageService fileStorageService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserStorageRepository userStorageRepository;

	@GetMapping({"/", ""})
	public RedirectView redirectToWelcomePage() {
		return new RedirectView("/control/welcome-page");
	}

	@GetMapping("/welcome-page")
	public String welcome() {
		return "index";
	}

	@GetMapping("/dashboard")
	public String dashboard(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);
		if(session == null){
			return "redirect:/control/login";
		}
		String userId = (String) session.getAttribute("userId");
		User user = userRepository.findByUserId(userId);
		model.addAttribute("userDetails", user);
		model.addAttribute("userName", user.getFirstName() + " " + user.getLastName());

		Map<String, Object> documentDetails = fileStorageService.getFileDetails(userId);
		model.addAttribute("totalDocsAndImages", documentDetails.get("totalDocsAndImg"));
		model.addAttribute("spaceOccupied", documentDetails.get("spaceOccupied"));
		model.addAttribute("totalDocsMap", documentDetails.get("totalDocsMap"));
		model.addAttribute("totalImagesMap", documentDetails.get("totalImagesMap"));

		try {
			List<Map<String, Object>> imagesData = filesRenderService.getUserDocuments(userId);
			model.addAttribute("userDocuments", imagesData);

			imagesData = filesRenderService.getRecentUserDocuments(userId);
			model.addAttribute("recentDocuments", imagesData);

			model.addAttribute("profile", filesRenderService.getProfileImage(userId));
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		return "dashboard/dashboard";

	}

	@PostMapping("getDocuments")
	public ResponseEntity<String> getDocuments(@RequestParam("fileCategory") String fileCategory, HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);

		if(session == null){
			return ResponseEntity.ok("Session expired.");
		}
		String userId = (String) session.getAttribute("userId");
		JSONObject resultObj = new JSONObject();
		try {
			JSONArray imagesData = filesRenderService.getDocuments(userId, fileCategory);
			System.out.println(imagesData);
			resultObj.put("data", imagesData);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		return ResponseEntity.ok(resultObj.toString());
	}

	@PostMapping("downloadDoc")
	public ResponseEntity<String> downloadDoc(@RequestParam("storageId") String storageId) {
		JSONObject file = filesRenderService.getDocumentByStorageId(storageId);
		return ResponseEntity.ok(file.toString());
	}

	@PostMapping("getDoc")
	public ResponseEntity<String> getDoc(@RequestParam("storageId") String storageId) {
		JSONObject file = filesRenderService.getDocumentByStorageId(storageId);
		return ResponseEntity.ok(file.toString());
	}

	@PostMapping("deleteDoc")
	public ResponseEntity<String> deleteDoc(@RequestParam("storageId") String storageId) {
		String message = fileStorageService.delectDoc(storageId);
		return ResponseEntity.ok(new JSONObject().put("message", message).toString());
	}

	@GetMapping("/login")
	public String login() {
			return "signin";
	}

	@GetMapping("/create-account")
	public String signup() {
		return "signup";
	}

	@GetMapping("/error")
	public String error() {
		return "error";
	}
	
}
