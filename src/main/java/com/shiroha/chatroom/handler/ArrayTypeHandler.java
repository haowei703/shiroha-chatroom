package com.shiroha.chatroom.handler;

import com.shiroha.chatroom.domain.GroupMemberDO;
import com.shiroha.chatroom.utils.JsonUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.OTHER)
@Component
public class ArrayTypeHandler extends BaseTypeHandler<List<GroupMemberDO>> {


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<GroupMemberDO> parameter, JdbcType jdbcType) throws SQLException {
        PGobject arrayObject = new PGobject();
        arrayObject.setType("array");
        String arrayString = "{" + parameter.stream()
                .map(Object::toString)
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.joining(",")) + "}";
        arrayObject.setValue(arrayString);
        ps.setObject(i, arrayObject);
    }

    @Override
    public List<GroupMemberDO> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String arrayString = rs.getString(columnName);
        return parseStringArray(arrayString);
    }

    @Override
    public List<GroupMemberDO> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String arrayString = rs.getString(columnIndex);
        return parseStringArray(arrayString);
    }

    @Override
    public List<GroupMemberDO> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String arrayString = cs.getString(columnIndex);
        return parseStringArray(arrayString);
    }

    private List<GroupMemberDO> parseStringArray(String arrayString) {
        if (arrayString == null || arrayString.isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.stream(arrayString.substring(1, arrayString.length() - 1).split(","))
                .map(String::trim)
                .map(this::parseElement)
                .collect(Collectors.toList());
    }

    private GroupMemberDO parseElement(String jsonString) {
        if (jsonString != null) {
            return new GroupMemberDO();
        }
        return null;
    }
}
