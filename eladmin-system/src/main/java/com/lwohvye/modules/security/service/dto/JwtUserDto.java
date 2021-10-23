/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lwohvye.modules.security.service.dto;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.lwohvye.modules.system.service.RoleService;
import com.lwohvye.modules.system.service.dto.UserInnerDto;
import com.lwohvye.utils.SpringContextHolder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Getter
@AllArgsConstructor
public class JwtUserDto implements UserDetails {

    private final UserInnerDto user;

    private final List<Long> dataScopes;

    // TODO: 2021/9/12 尚未确定不做序列化的原因，但若放到redis中，需要进行序列化
//    @JSONField(serialize = false)
    private final List<GrantedAuthority> authorities;

    public Set<String> getRoles() {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
    }

    @Override
    @JSONField(serialize = false)
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    @JSONField(serialize = false)
    public String getUsername() {
        return user.getUsername();
    }

    public List<GrantedAuthority> getAuthorities() {
        authorities.clear();
        authorities.addAll(SpringContextHolder.getBean(RoleService.class)
                .mapToGrantedAuthorities(user.getId(), user.getIsAdmin())
                .stream().map(grantedAuthorityObj -> {
                    if(grantedAuthorityObj instanceof GrantedAuthority grantedAuthority)
                        return grantedAuthority;
                    // TODO: 2021/10/23 先简单处理，从缓存中取出时，会丢失类型信息，变成JSONObject。当前只用到SimpleGranteAuthority,后续用到别的需同步调整
                    if (grantedAuthorityObj instanceof JSONObject authorityJon)
                        return authorityJon.toJavaObject(SimpleGrantedAuthority.class);
                    return grantedAuthorityObj;
                }).toList());
        return authorities;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JSONField(serialize = false)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JSONField(serialize = false)
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
