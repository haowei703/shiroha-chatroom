package com.shiroha.chatroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shiroha.chatroom.domain.EmojiDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface EmojiMapper extends BaseMapper<EmojiDO> {
}
