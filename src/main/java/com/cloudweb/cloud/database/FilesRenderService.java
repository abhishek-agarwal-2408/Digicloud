package com.cloudweb.cloud.database;

import com.cloudweb.cloud.authentication.user.User;
import com.cloudweb.cloud.authentication.user.UserRepository;
import org.apache.tika.Tika;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilesRenderService {

    @Autowired
    private UserStorageRepository userStorageRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${baseFolder}") // Load the baseFolder value from application.properties
    private String baseFolder;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public List<Map<String, Object>> getUserDocuments(String userId) {
        Set<UserStorage> userStorages = userStorageRepository.findByUserId(userId);

        List<Map<String, Object>> fileDetailsList = new ArrayList<>();

        try {
            for (UserStorage userStorage : userStorages) {
                String filePath = baseFolder + userId + "/" + userStorage.getLocation();
                Path file = Paths.get(filePath);
                byte[] imageData = Files.readAllBytes(file);
                String encodedImageData = Base64.getEncoder().encodeToString(imageData);

                Map<String, Object> fileDetails = new HashMap<>();
                fileDetails.put("fileName", userStorage.getFileName());
                fileDetails.put("fileType", userStorage.getFileType());
                fileDetails.put("storageId", userStorage.getStorageId());
                fileDetails.put("base64Data", encodedImageData);
                fileDetails.put("size", decimalFormat.format(bytesToMB(userStorage.getSize())) + " MB");
                fileDetails.put("dateTime", userStorage.getCreatedDateTime().format(formatter));
                fileDetailsList.add(fileDetails);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileDetailsList;
    }

    public JSONObject getDocumentByStorageId(String storageId) {
        UserStorage userStorage = userStorageRepository.findByStorageId(storageId);
        String filePath = baseFolder + userStorage.getUser().getUserId() + "/" + userStorage.getLocation();
        JSONObject fileDetails = new JSONObject();
        try {
            Path file = Paths.get(filePath);
            byte[] imageData = Files.readAllBytes(file);
            String encodedImageData = Base64.getEncoder().encodeToString(imageData);
            fileDetails.put("fileName", userStorage.getFileName());
            fileDetails.put("mimeType", userStorage.getFileType());
            fileDetails.put("base64Data", encodedImageData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileDetails;
    }

    public JSONArray getDocuments(String userId, String category) {
        Set<UserStorage> userStorages = "all".equalsIgnoreCase(category) ? userStorageRepository.findByUserId(userId) : userStorageRepository.findByUserIdAndCategory(userId, category);
        JSONArray fileDetailsArr = new JSONArray();
        for (UserStorage userStorage : userStorages) {
            JSONObject fileDetails = new JSONObject();
            fileDetails.put("fileName", userStorage.getFileName());
            fileDetails.put("fileCategory", userStorage.getFileCategory());
            fileDetails.put("storageId", userStorage.getStorageId());
            fileDetails.put("size", decimalFormat.format(bytesToMB(userStorage.getSize())) + " MB");
            fileDetails.put("dateTime", userStorage.getCreatedDateTime().format(formatter));
            fileDetailsArr.put(fileDetails);
        }
        return fileDetailsArr;
    }

    public List<Map<String, Object>> getRecentUserDocuments(String userId) {
        Set<UserStorage> sortedStorages = userStorageRepository.findByUserId(userId);

        List<UserStorage> userStorages = sortedStorages.stream()
                .sorted(Comparator.comparing(UserStorage::getCreatedDateTime).reversed())
                .collect(Collectors.toList());

        List<Map<String, Object>> fileDetailsList = new ArrayList<>();

        int count = 0, limit = 10;
        for (UserStorage userStorage : userStorages) {
            if (count == limit) {
                break; // Stop after fetching the top 'limit' documents
            }
            count++;
            String filePath = baseFolder + userId + "/" + userStorage.getLocation();
            try {
                Path file = Paths.get(filePath);
                byte[] imageData = Files.readAllBytes(file);
                String encodedImageData = Base64.getEncoder().encodeToString(imageData);

                Map<String, Object> fileDetails = new HashMap<>();
                fileDetails.put("fileName", userStorage.getFileName());
                fileDetails.put("storageId", userStorage.getStorageId());
                fileDetails.put("mimeType", userStorage.getFileType());
                fileDetails.put("base64Data", encodedImageData);
                fileDetails.put("dateTime", userStorage.getCreatedDateTime().format(formatter));

                fileDetailsList.add(fileDetails);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileDetailsList;
    }

    public Map<String, Object> getProfileImage(String userId) {
        Map<String, Object> profileImageDetails = new HashMap<>();
        User user = userRepository.findByUserId(userId);
        String imageName = (user.getProfileImageName() == null || user.getProfileImageName().isEmpty()) ? "mail-avatar.jpeg" : user.getProfileImageName();
        String image = baseFolder + "profiles/" + imageName;
        try {
            Path file = Paths.get(image);
            Tika tika = new Tika();
            byte[] imageData = Files.readAllBytes(file);
            String encodedImageData = Base64.getEncoder().encodeToString(imageData);

            profileImageDetails.put("base64", encodedImageData);
            profileImageDetails.put("mimeType", tika.detect(imageData));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return profileImageDetails;
    }
    public static double bytesToMB(long bytes) {
        return (double) bytes / (1024 * 1024);
    }
}
