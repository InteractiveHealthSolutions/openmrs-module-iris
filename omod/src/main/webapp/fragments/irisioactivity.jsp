<%@ include file="/WEB-INF/template/include.jsp"%>
<style>
<!--
.ivdiv {
	padding: 20px;
	border: solid thin silver;
}
-->
</style>
<script type="text/javascript">
function pullData(){
	loadJsonWOJQuery('${pageContext.request.contextPath}/module/iris/iohandler/pullIrisData.form',
		function(data) { showSuccess(data.message); },
		function(xhr) { showError(xhr); }
	);
}
function pushData(){
	loadJsonWOJQuery('${pageContext.request.contextPath}/module/iris/iohandler/pushDataToIris.form',
		function(data) { showSuccess(data.message); },
		function(xhr) { showError(xhr); }
	);
}

function showError(msg){
	document.getElementById('msgDiv').innerHTML='<span style="color:red">'+msg+'</span>';
}
function showSuccess(msg){
	document.getElementById('msgDiv').innerHTML='<span style="color:green">'+msg+'</span>';
}

function loadJsonWOJQuery(path, success, error)
{
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function()
    {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                if (success)
                    success(JSON.parse(xhr.responseText));
            } else {
                if (error)
                    error(xhr);
            }
        }
    };
    xhr.open("GET", path, true);
    xhr.send();
}
</script>
<div id="msgDiv">${message}</div>
<form>
<table style="border: 0px">
	<tr>
		<td>
			<h1>IRIS</h1>
			<br>
			IRIS is an external tool that finds out Cause of Death (COD) for death certificates filled according to International Medical Death Certificates pattern.
            <br>
            eCODIRS is using IRIS to calculate COD from the information filled into Medical Death Record forms. 
			<br>
            It is a two step process and is explained below. 
            To download IRIS and read further details about the software, visit: <a href="http://www.dimdi.de/static/en/klassi/irisinstitute" >http://www.dimdi.de/static/en/klassi/irisinstitute</a></td>
	</tr>
	<tr>
		<td>
			<div class="ivdiv">
			<h2>Step 1: Pushing eCODIRS data to IRIS</h2>
			<br>
			eCODIRS needs to first push data to IRIS for COD calculations to be done. 
			<br>
			Click 'Push Data to IRIS' button below to do this. 
			<br><br>
			<input type="button" value="Push Data to IRIS" onclick="pushData();"/>
			</div>
		</td>
	</tr>
	<tr>
		<td>
			<div class="ivdiv">
			<h2>Step 2: Pulling Processed data Back to eCODIRS</h2>
			<br>
			When IRIS software has calculated COD, the result needs to be pulled back into eCODIRS. 
			This COD will appear in reports and eCODIRS database.
			<br>
			Click 'Pull COD to eCODIRS' button below to do this. 	
			<br><br>
			<input type="button" value="Pull COD to eCODIRS" onclick="pullData();"/>
			</div>
		</td>
	</tr>
</table>
</form>
