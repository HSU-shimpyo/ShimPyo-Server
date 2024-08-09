package com.hsu.shimpyoo.global.user.repository;

import com.hsu.shimpyoo.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
