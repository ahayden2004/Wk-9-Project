package project.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import projects.entity.Project;
import project.exception.DbException;
import provided.util.DaoBase;

/**
 * Data Access Object for managing Project data in the database.
 */
public class ProjectDao extends DaoBase {
	private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";
     
    public void executeBatch(List<String> sqlBatch) {
        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (Statement stmt = conn.createStatement()) {
                for (String sql : sqlBatch) {
                    stmt.addBatch(sql);
                }
                stmt.executeBatch();
                commitTransaction(conn);
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    /**
     * Inserts a project into the database.
     * 
     * @param project The Project object to insert
     * @return The Project object with the generated ID
     */
    public Project insertProject(Project project) {
        String sql = "INSERT INTO " + PROJECT_TABLE + " "
            + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
            + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                // Execute the update
                stmt.executeUpdate();
                
               Integer projectId = getLastInsertId(conn, PROJECT_TABLE); 
               commitTransaction(conn);
               
               project.setProjectId(projectId);
               return project;

                
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    private void setParameter(PreparedStatement stmt, int index, Object value) throws SQLException {
        if (value instanceof Integer) {
            stmt.setInt(index, (Integer) value);
        } else if (value instanceof BigDecimal) {
            stmt.setBigDecimal(index, (BigDecimal) value);
        } else if (value instanceof String) {
            stmt.setString(index, (String) value);
        } else {
            stmt.setObject(index, value); // Handle other types
        }
    }

    // Other methods...

    public static String getCategoryTable() {
        return CATEGORY_TABLE;
    }

    public static String getMaterialTable() {
        return MATERIAL_TABLE;
    }

    public static String getProjectCategoryTable() {
        return PROJECT_CATEGORY_TABLE;
    }

    public static String getStepTable() {
        return STEP_TABLE;
    }
}
	

