package com.example.myInst.services;

import com.example.myInst.entity.ImageModel;
import com.example.myInst.entity.Post;
import com.example.myInst.entity.User;
import com.example.myInst.exceptions.ImageNotFoundException;
import com.example.myInst.repository.ImageRepository;
import com.example.myInst.repository.PostRepository;
import com.example.myInst.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.crypto.Data;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
public class ImageUploadService {

    public static final Logger LOG = LoggerFactory.getLogger(ImageUploadService.class);

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public ImageUploadService(ImageRepository imageRepository, UserRepository userRepository, PostRepository postRepository) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public ImageModel uploadImageToUser(MultipartFile multipartFile, Principal principal) throws IOException {
        User user = getUserByPrincipal(principal);
        LOG.info("Upload image profile to User {} ", user.getUsername());

        ImageModel userProfileImage = imageRepository.findByUserId(user.getId()).orElse(null);
        if(!ObjectUtils.isEmpty(userProfileImage)){
            imageRepository.delete(userProfileImage);
        }

        ImageModel imageModel = new ImageModel();
        imageModel.setUserId(user.getId());
        imageModel.setImagesBytes(compressBytes(multipartFile.getBytes()));
        imageModel.setName(multipartFile.getOriginalFilename());
        return imageRepository.save(imageModel);
    }

    public ImageModel uploadImageToPost(MultipartFile multipartFile, Principal principal, Long postId) throws IOException{
        User user = getUserByPrincipal(principal);
        Post post = user.getPosts()
                .stream()
                .filter(p -> p.getId().equals(postId))
                .collect(toSinglePostCollector());

        ImageModel imageModel = new ImageModel();
        imageModel.setPostId(post.getId());
        imageModel.setImagesBytes(compressBytes(multipartFile.getBytes()));
        imageModel.setName(multipartFile.getOriginalFilename());
        LOG.info("Upload image to Post {} ", post.getId());
        return imageRepository.save(imageModel);
    }

    public ImageModel getImageToUser(Principal principal){
        User user = getUserByPrincipal(principal);

        ImageModel imageModel = imageRepository.findByUserId(user.getId()).orElse(null);
        if (!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImagesBytes(decompressBytes(imageModel.getImagesBytes()));
        }

        return imageModel;
    }

    public ImageModel getImageToPost(Long postId){
        ImageModel imageModel = imageRepository.findByPostId(postId)
                .orElseThrow(() -> new ImageNotFoundException("Cannot file image to Post: " + postId));

        if (!ObjectUtils.isEmpty(imageModel)){
            imageModel.setImagesBytes(decompressBytes(imageModel.getImagesBytes()));
        }

        return imageModel;
    }

    private byte[] compressBytes(byte[] date){
        Deflater deflater = new Deflater();
        deflater.setInput(date);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(date.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()){
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            LOG.error("Cannot compress Bytes");
        }
        System.out.println("Compresses Image Byte Size - " + outputStream.toByteArray().length);
        return outputStream.toByteArray();
    }

    private static byte[] decompressBytes(byte[] date){
        Inflater inflater = new Inflater();
        inflater.setInput(date);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(date.length);
        byte[] buffer = new byte[1024];
        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException e) {
            LOG.error("Cannot decompress Bytes");
        }
        return outputStream.toByteArray();
    }

    private User getUserByPrincipal(Principal principal){
        String username = principal.getName();
        return userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username" + username));
    }

    private <T> Collector<T, ?, T> toSinglePostCollector(){
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if(list.size() != 1){
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
