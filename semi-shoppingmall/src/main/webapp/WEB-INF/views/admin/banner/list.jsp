<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<jsp:include page="/WEB-INF/views/template/header.jsp"/>

<div class="container w-800">
    <h1>배너 목록</h1>

    <div class="cell right">
        <a href="add" class="btn btn-positive">배너 등록</a>
    </div>

    <table class="table table-hover mt-20">
        <thead>
            <tr>
                <th>제목</th>
                <th>이미지</th>
                <th>순서</th>
                <th>링크</th>
                <th>관리</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="banner" items="${bannerList}">
                <tr>
                    <td>${banner.bannerTitle}</td>
                    <td>
                        <c:if test="${not empty banner.bannerAttachmentNo}">
                            <img src="/attachment/view?attachmentNo=${banner.bannerAttachmentNo}"
                                 width="120" height="60"
                                 style="object-fit:cover; border:1px solid #ccc; border-radius:6px;">
                        </c:if>
                        <c:if test="${empty banner.bannerAttachmentNo}">
                            <span style="color:#aaa;">이미지 없음</span>
                        </c:if>
                    </td>
                    <td>${banner.bannerOrder}</td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty banner.bannerLink}">
                                <a href="${banner.bannerLink}" target="_blank">${banner.bannerLink}</a>
                            </c:when>
                            <c:otherwise>
                                <span style="color:#aaa;">링크 없음</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td>
                        <a href="edit?bannerNo=${banner.bannerNo}" class="btn btn-neutral btn-sm">수정</a>
                        <a href="delete?bannerNo=${banner.bannerNo}" class="btn btn-negative btn-sm"
                           onclick="return confirm('정말 삭제하시겠습니까?');">삭제</a>
                    </td>
                </tr>
            </c:forEach>

            <c:if test="${empty bannerList}">
                <tr>
                    <td colspan="5" class="center">등록된 배너가 없습니다.</td>
                </tr>
            </c:if>
        </tbody>
    </table>
</div>

<jsp:include page="/WEB-INF/views/template/footer.jsp"/>
