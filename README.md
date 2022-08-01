# Scala-SPA-API
<p>Scala Web API to serve a single page application which allows a user to create and edit web content through the browser.</p>
<p>The API supports multi-tiered user authentication and uses a mongo database using the robomongo scala library for non blocking I/O access.</p>
<p>The scala api and the frontend server are run in seperate docker containers on the same network.  The frontend is served directly by nginx which also acts as a reverse proxy for the API.  </p>


