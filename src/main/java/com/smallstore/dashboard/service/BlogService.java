package com.smallstore.dashboard.service;

import com.smallstore.dashboard.dto.BlogRequest;
import com.smallstore.dashboard.entity.Blog;
import com.smallstore.dashboard.entity.BlogImage;
import com.smallstore.dashboard.entity.BlogStatus;
import com.smallstore.dashboard.entity.Tag;
import com.smallstore.dashboard.repository.BlogImageRepository;
import com.smallstore.dashboard.repository.BlogRepository;
import com.smallstore.dashboard.repository.TagRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlogService {
    private final BlogRepository blogRepository;
    private final TagRepository tagRepository;
    private final BlogImageRepository blogImageRepository;

    @Autowired
    public BlogService(BlogRepository blogRepository, TagRepository tagRepository, BlogImageRepository blogImageRepository) {
        this.blogRepository = blogRepository;
        this.tagRepository = tagRepository;
        this.blogImageRepository = blogImageRepository;
    }

    @Transactional
    public Blog createBlog(BlogRequest blogRequest) {
        log.info("transaction createBlog started");
        Blog blog = new Blog();
        blog.setBlogTitle(blogRequest.getTitle());
        blog.setBlogContent(blogRequest.getContent());
        blog.setCreatedBy(blogRequest.getCreatedBy());
        blog.setStatus(blogRequest.getStatus()!=null ? blogRequest.getStatus() : BlogStatus.valueOf(BlogStatus.DRAFT.name().toUpperCase()));
        Set<String> ff = new HashSet<>();
        blogRequest.getTags().forEach(taggi -> ff.add(taggi.toString()));
        // Handle Tags
        Set<Tag> tags = blogRequest.getTags().stream()
                .map(tagName -> tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName))))
                .collect(Collectors.toSet());
        blog.setTags(tags);

        // Save Blog First (So we get the ID)
        Blog savedBlog = blogRepository.save(blog);

        // Handle Images
        List<BlogImage> blogImages = blogRequest.getBlogImages().stream()
                .map(url -> {
                    BlogImage image = new BlogImage();
                    image.setImageUrl(url.getImageUrl());
                    image.setImageOrder(url.getImageOrder());
                    image.setBlog(savedBlog);
                    return image;
                }).collect(Collectors.toList());

        blogImageRepository.saveAll(blogImages);
        savedBlog.setBlogImages(blogImages);

        return blogRepository.save(savedBlog);
    }

    public List<Blog> getAllBlogs() {
        List<Blog> blogs = blogRepository.findAll();
        return blogs;
    }

    public Blog findByBlogId(String id) {
        return blogRepository.findById(Long.valueOf(id)).orElseThrow();
    }
}

