package io.github.abdofficehour.appointmentsystem.pojo.enumclass;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(Aim.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class AimTypeHandler extends BaseTypeHandler<Aim> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Aim parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.getValue());
    }

    @Override
    public Aim getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return Aim.fromValue(value);
    }

    @Override
    public Aim getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return Aim.fromValue(value);
    }

    @Override
    public Aim getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return Aim.fromValue(value);
    }
}
