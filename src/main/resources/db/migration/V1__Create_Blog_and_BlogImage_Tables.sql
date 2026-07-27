-- Create Enum Type for Blog Status (Only for PostgreSQL)
DO $$ BEGIN
    CREATE TYPE blog_status AS ENUM ('DRAFT', 'PUBLISHED', 'ARCHIVED');
EXCEPTION
    WHEN duplicate_object THEN null;
END $$;

DO $$ BEGIN
    CREATE CAST (varchar AS blog_status) WITH INOUT AS IMPLICIT;
EXCEPTION
    WHEN duplicate_object THEN NULL;
END $$;

-- Create Blog Table
CREATE TABLE IF NOT EXISTS public.blog (
    id SERIAL PRIMARY KEY,
    blog_title VARCHAR(255) UNIQUE NOT NULL,
    slug VARCHAR(255) UNIQUE NOT NULL,
    blog_content TEXT NOT NULL,
    status blog_status NOT NULL DEFAULT 'DRAFT',
    create_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    view_count INT NOT NULL DEFAULT 0,
    like_count INT NOT NULL DEFAULT 0
);

-- Create Blog Image Table
CREATE TABLE IF NOT EXISTS public.blog_image (
    id SERIAL PRIMARY KEY,
    image_url TEXT NOT NULL,
    image_order INT NOT NULL,
    blog_id BIGINT NOT NULL,
    CONSTRAINT fk_blog FOREIGN KEY (blog_id) REFERENCES blog(id) ON DELETE CASCADE
);

-- Create Tag Table
CREATE TABLE IF NOT EXISTS public.tag (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) UNIQUE NOT NULL
);

-- Create Blog_Tags Join Table
CREATE TABLE IF NOT EXISTS public.blog_tags (
    blog_id BIGINT NOT NULL,
    tag_id BIGINT NOT NULL,
    PRIMARY KEY (blog_id, tag_id),
    CONSTRAINT fk_blog FOREIGN KEY (blog_id) REFERENCES blog(id) ON DELETE CASCADE,
    CONSTRAINT fk_tag FOREIGN KEY (tag_id) REFERENCES tag(id) ON DELETE CASCADE
);

