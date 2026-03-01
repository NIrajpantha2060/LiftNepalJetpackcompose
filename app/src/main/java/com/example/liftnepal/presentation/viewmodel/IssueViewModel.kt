package com.example.liftnepal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liftnepal.data.model.Issue
import com.example.liftnepal.data.repository.IssueRepository
import com.example.liftnepal.data.utils.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class IssueViewModel : ViewModel() {

    private val repository = IssueRepository()

    private val _submitIssueState = MutableStateFlow<Result<Boolean>?>(null)
    val submitIssueState: StateFlow<Result<Boolean>?> = _submitIssueState

    private val _allIssuesState = MutableStateFlow<Result<List<Issue>>?>(null)
    val allIssuesState: StateFlow<Result<List<Issue>>?> = _allIssuesState

    private val _userIssuesState = MutableStateFlow<Result<List<Issue>>?>(null)
    val userIssuesState: StateFlow<Result<List<Issue>>?> = _userIssuesState

    private val _updateStatusState = MutableStateFlow<Result<Boolean>?>(null)
    val updateStatusState: StateFlow<Result<Boolean>?> = _updateStatusState

    fun submitIssue(issue: Issue) {
        viewModelScope.launch {
            _submitIssueState.value = Result.Loading
            val result = repository.submitIssue(issue)
            _submitIssueState.value = result
            // Refresh user issues if submission is successful
            if (result is Result.Success) {
                fetchUserIssues(issue.userId)
            }
        }
    }

    fun fetchAllIssues() {
        viewModelScope.launch {
            _allIssuesState.value = Result.Loading
            _allIssuesState.value = repository.getAllIssues()
        }
    }

    fun fetchUserIssues(userId: String) {
        viewModelScope.launch {
            _userIssuesState.value = Result.Loading
            _userIssuesState.value = repository.getIssuesByUser(userId)
        }
    }

    // ✅ Now accepts adminRemarks
    fun resolveIssue(issueId: String, adminRemarks: String = "") {
        viewModelScope.launch {
            _updateStatusState.value = Result.Loading
            val result = repository.updateIssueStatus(issueId, "resolved", adminRemarks)
            _updateStatusState.value = result
            if (result is Result.Success) fetchAllIssues()
        }
    }

    fun clearSubmitState() { _submitIssueState.value = null }
    fun clearUpdateStatusState() { _updateStatusState.value = null }
}