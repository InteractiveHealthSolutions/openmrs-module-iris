<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>
<style>
<!--
table.datatableiva {
	border: 1px gray solid;
	border-spacing: 0px;
	border-collapse: collapse;
	margin: 2px;
	width: 100%;
}

table.datatableiva td {
	border: 1px #F2F2F2 solid;
	padding: 1px;
	font-size: 12px;
}

table.datatableiva th {
	border: 1px #F2F2F2 solid;
	padding: 1px;
}
.heading{
	font-size: large;
	font-weight: bold;
	color: black;
}
-->
</style>
<script type="text/javascript">
<!--
function showIvaMappingDetails(objid){
	var detailstr = "<html><head><title>IRIS INPUT Mapping ("+objid+")</title></head>"+
					"<style>"+
					"table.datatableiva { border: 1px gray solid; border-collapse: collapse; }"+
					"table.datatableiva td { border: 1px #F2F2F2 solid; }"+
					"table.datatableiva th { border: 1px #F2F2F2 solid; text-align: left}"+
					".heading{ font-size: large; font-weight: bold; color: black; background-color: silver;}"+
					"</style><body>"+
					"<div class='heading'>IRIS INPUT Mapping</div>"+
					"<table class='datatableiva'>";
	<c:forEach items="${inputmappings}" var="item" varStatus="loop"> 
    	if('${item.irisMappingId}' == (''+objid)){
    		detailstr += 
    			"<tr><th>Mapping ID </th><td>${item.irisMappingId}</td></tr>"+
    			"<tr><th>Variable name</th><td>${item.irisVariable}</td></tr>"+
    		   	"<tr><th>Index</th><td>${item.irisIndex}</td></tr>"+
    		   	"<tr><th>Concept ID</th><td>${item.concept.conceptId}</td></tr>"+
    		   	"<tr><th>Concept</th><td>${item.concept.name}</td></tr>"+
    		   	"<tr><th>Observation</th><td>${item.observation}</td></tr>"+
    		   	"<tr><th>Observation Accept Condition</th><td>${item.acceptCondition}</td></tr>"+
    		   	"<tr><th>Creator</th><td>${item.creator.username}</td></tr>"+
    		   	"<tr><th>Date Created</th><td>${item.dateCreated}</td></tr>"+
    		   	"<tr><th>Changed By</th><td>${item.changedBy.username}</td></tr>"+
    		   	"<tr><th>Date Changed</th><td>${item.dateChanged}</td></tr>"+
    		   	"<tr><th>Voided</th><td>${item.voided}</td></tr>"+
    		   	"<tr><th>Date Voided</th><td>${item.dateVoided}</td></tr>"+
    		   	"<tr><th>Voided By</th><td>${item.voidedBy.username}</td></tr>"+
    		   	"<tr><th>Void Reason</th><td>${item.voidReason}</td></tr>"+
    		   	"<tr><th>Description</th> <td>${item.description}</td></tr>";
    	}
  	</c:forEach>  
  	detailstr += "</table></body></html>";
  	var win;

	win=window.open('','IVA INPUT MAPPING-ID:'+objid,'width=600,height=700,resizable=no,toolbar=no,location=no,scrollbars=yes,directories=no,status=no,menubar=no,copyhistory=no');
	win.document.write(detailstr);
	win.focus();
}
//-->
</script>
<div class="heading">IRIS Mappings for INPUT CSV file</div>
<div id="detailsDiv" class="">${message}</div>
<div style="float: right;"><a href="${pageContext.request.contextPath}/module/iris/inputmappings/generateInputMappingConcepts.form"></a></div>
<table class="datatableiva">
  <tr>
   <th></th>
   <th>Mapping ID</th>
   <th>Variable name</th>
   <th>Index</th>
   <th>Concept ID</th>
   <th>Concept</th>
   <th>Observation</th>
   <th>Observation Accept Condition</th>
   <!-- <th>Creator</th> -->
   <!-- <th>Date Created</th> -->
   <!-- <th>Changed By</th>
   <th>Date Changed</th> -->
   <th>Voided</th>
   <!-- <th>Date Voided</th> -->
   <!-- <th>Voided By</th> -->
   <th>Void Reason</th>
   <th>Description</th>
  </tr>
  <c:forEach var="iml" items="${inputmappings}">
      <tr>
      	<td><a onclick="showIvaMappingDetails('${iml.irisMappingId}');">details</a></td>
        <td>${iml.irisMappingId}</td>
        <td>${iml.irisVariable}</td>
        <td>${iml.irisIndex}</td>
        <td>${iml.concept.conceptId}</td>
        <td style="text-transform: capitalize">${fn:toLowerCase(iml.concept.name)}</td>
        <td>${iml.observation}</td>
        <td>${iml.acceptCondition}</td>
       <!--  <td></td> -->
        <%-- <td>${iml.dateCreated}</td> --%>
       <%--  <td></td>
        <td>${iml.dateChanged}</td> --%>
        <td>${iml.voided}</td>
       <%--  <td>${iml.dateVoided}</td> --%>
        <!-- <td></td> -->
        <td>${iml.voidReason}</td>
        <td>${iml.description}</td>
      </tr>		
  </c:forEach>
</table>

<%@ include file="/WEB-INF/template/footer.jsp"%>
