package taegeuni.github.project_justrun.repository

import org.springframework.data.jpa.repository.JpaRepository
import taegeuni.github.project_justrun.entity.AssignmentSubmission

interface AssignmentSubmissionRepository : JpaRepository<AssignmentSubmission, Int> {
    fun findByAssignmentAssignmentIdInAndStudentUserId(
        assignmentIds: List<Int>,
        studentId: Int
    ): List<AssignmentSubmission>
}
