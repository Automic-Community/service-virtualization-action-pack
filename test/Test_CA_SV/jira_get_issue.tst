<?xml version="1.0" ?>

<TestCase name="jira_get_issue" version="5">

<meta>
   <create version="10.0.0" buildNumber="10.0.0.431" author="admin" date="03/27/2017" host="vviespartan03" />
   <lastEdited version="10.0.0" buildNumber="10.0.0.431" author="admin" date="03/29/2017" host="vviespartan03" />
</meta>

<id>BF0E5F6E12B511E787B1005056BD12C1</id>
<Documentation>Put documentation of the Test Case here.</Documentation>
<IsInProject>true</IsInProject>
<sig>ZWQ9NSZ0Y3Y9NSZsaXNhdj0xMC4wLjAgKDEwLjAuMC40MzEpJm5vZGVzPTE5NTg3NTUzMzg=</sig>
<subprocess>false</subprocess>

<initState>
</initState>

<resultState>
</resultState>

<deletedProps>
</deletedProps>

    <Node name="http GET /rest/api/2/issue/ROC-157" log=""
          type="com.itko.lisa.ws.rest.RESTNode" 
          version="3" 
          uid="C2A393C112B511E787B1005056BD12C1" 
          think="500-1S" 
          useFilters="true" 
          quiet="false" 
          next="end" > 

<url>http://{{LIVE_INVOCATION_SERVER}}:8080/rest/api/2/issue/ROC-157</url>
<content-type></content-type>
<data-type>text</data-type>
      <header field="Authorization" value="Basic YWRtaW46QWRtaW4xMjMh" />
<httpMethod>GET</httpMethod>
<onError>abort</onError>
<encode-test-props-in-url>true</encode-test-props-in-url>
    </Node>


    <Node name="end" log=""
          type="com.itko.lisa.test.NormalEnd" 
          version="1" 
          uid="BF0E5F7412B511E787B1005056BD12C1" 
          think="0h" 
          useFilters="true" 
          quiet="true" 
          next="fail" > 

    </Node>


    <Node name="fail" log=""
          type="com.itko.lisa.test.Abend" 
          version="1" 
          uid="BF0E5F7212B511E787B1005056BD12C1" 
          think="0h" 
          useFilters="true" 
          quiet="true" 
          next="abort" > 

    </Node>


    <Node name="abort" log=""
          type="com.itko.lisa.test.AbortStep" 
          version="1" 
          uid="BF0E5F7012B511E787B1005056BD12C1" 
          think="0h" 
          useFilters="true" 
          quiet="true" 
          next="" > 

    </Node>


</TestCase>
