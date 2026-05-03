package com.horizonix.nboard.service;

import com.horizonix.nboard.entity.User;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {

    public boolean isOwnerVerified(User user) {
        return user.isVerified();
    }
}
