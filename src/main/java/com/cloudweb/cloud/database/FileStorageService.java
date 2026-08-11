package com.cloudweb.cloud.database;

import com.cloudweb.cloud.authentication.user.User;
import com.cloudweb.cloud.authentication.user.UserRepository;
import com.cloudweb.cloud.exception.StorageUsedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageService {
    @Autowired
    private StorageId storageIdGenerator;

    @Autowired
    UserStorageRepository userStorageRepository;

    @Autowired
    UserRepository userRepository;

    @Value("${baseFolder}") // Load the baseFolder value from application.properties
    private String baseFolder;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    public UserStorage storeFile(MultipartFile file, String userId){
        UserStorage userStorage = new UserStorage();
        try {
            User user = userRepository.findByUserId(userId);
            String fileCategory = file.getContentType().contains("image") ? "image" : "document";
            String storageId = storageIdGenerator.generateId(fileCategory);

            userStorage.setSize(file.getSize());
            userStorage.setFileCategory(fileCategory);
            userStorage.setFileType(file.getContentType());
            userStorage.setFileName(file.getOriginalFilename());
            userStorage.setUser(user);
            userStorage.setDeleted("N");
            userStorage.setStorageId(storageId);
            userStorage.setCreatedDateTime(LocalDateTime.now());

            System.out.println("Storing documents.... ");
            long totalSize = userStorageRepository.getTotalSizeByUserId(userId);
            Double storageOccupied = bytesToGB(totalSize);
            if(storageOccupied >= 1){
                throw new StorageUsedException("You have used your 1 gb storage");
            }

            String dateTimeString = userStorage.getCreatedDateTime().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

            // Create the folder for the user if it doesn't exist
            Path userFolder = Paths.get(baseFolder + userId);
            if (!Files.exists(userFolder)) {
                    Files.createDirectories(userFolder);
            }

            //  storing files
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filePath = baseFolder + userId + "/" + userStorage.getStorageId() + "_" + dateTimeString + "." + fileExtension;
            Files.write(Paths.get(filePath), file.getBytes());

            userStorage.setLocation(userStorage.getStorageId() + "_" + dateTimeString + "." + fileExtension);
            userStorageRepository.save(userStorage);

        } catch (Exception e) {
            System.out.println(e);
        }
        return userStorage;
    }

    public String updateProfile(MultipartFile profile, String userId){
        try {
            User user = userRepository.findByUserId(userId);
            System.out.println("Updating profile image .... ");
            System.out.println("Profile image name: "+ profile.getOriginalFilename());
            // Create the folder for the user if it doesn't exist
            Path userFolder = Paths.get(baseFolder + "profiles");
            if (!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
            }

            //  storing files
            String fileExtension = StringUtils.getFilenameExtension(profile.getOriginalFilename());
            String filePath = userFolder + "/" + userId + "_profile." + fileExtension;
            Files.write(Paths.get(filePath), profile.getBytes());
            user.setProfileImageName(userId + "_profile." + fileExtension);
            userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e);
            return "error";
        }
        return "success";
    }

    private double bytesToGB(long bytes) {
        return (double) bytes / (1024 * 1024 * 1024); // Convert bytes to GB
    }

    public String delectDoc(String storageId) {
        UserStorage userStorage = userStorageRepository.findByStorageId(storageId);
        userStorage.setDeleted("Y");
        userStorageRepository.save(userStorage);
        return "Deleted successfully.";
    }
    public Map<String, Object> getFileDetails(String userId){
        Map<String, Object> result = new HashMap<>();
        long totalDocsAndImg = userStorageRepository.getTotalDocsByUserId(userId);
        result.put("totalDocsAndImg", totalDocsAndImg);

        long spaceOccupied = userStorageRepository.getTotalSizeByUserId(userId);
        result.put("spaceOccupied", decimalFormat.format(FilesRenderService.bytesToMB(spaceOccupied)) + " MB");

        Map<String, Object> totalDocsMap = new HashMap<>();
        long totalDocs = userStorageRepository.getNoOfDocsByUserIdAndCategory(userId, "document");
        double totalDocsPercentage = totalDocsAndImg > 0 ? (totalDocs * 100 / totalDocsAndImg) : 0;
        System.out.println(totalDocsPercentage);
        totalDocsMap.put("totalDocs", totalDocs);
        totalDocsMap.put("totalDocsPercentage", totalDocsPercentage);
        result.put("totalDocsMap", totalDocsMap);

        Map<String, Object> totalImagesMap = new HashMap<>();
        long totalImages = userStorageRepository.getNoOfDocsByUserIdAndCategory(userId, "image");
        double totalImagePercentage = totalDocsAndImg > 0 ? (totalImages  * 100 / totalDocsAndImg) : 0;
        System.out.println(totalImagePercentage);
        totalImagesMap.put("totalImages", totalImages);
        totalImagesMap.put("totalImagePercentage", totalImagePercentage);
        result.put("totalImagesMap", totalImagesMap);

        return result;
    }
}