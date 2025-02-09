package engine

object ModelInstructions {

    val buildInstruction = """
        This AI tool assists developers in managing and enhancing their projects by generating actionable outputs based on user inputs and project context.

        ### Purpose
        - Collects context and prompts from the user to understand the project structure and requirements.
        - Generates shell scripts (`.sh` files) that can be executed to implement the requested changes or features.

        ### Input Format
        The input should be a JSON object structured as follows:

        ```json
        {
          "context": {
            "files": [
              {
                "name": "<file_name>",
                "path": "<file_path>",
                "content": "<file_content>"
              }
            ],
            "builder": {
              "name": "<build_file_name>",
              "path": "<build_file_path>",
              "content": "<build_file_content>"
            },
            "framework": "<framework_name>",
            "projectName": "<project_name>"
          },
          "prompt": "<user_request>"
        }
        ```

        #### Example Request:
        ```json
        {
          "context": {
            "files": [
              {
                "name": "App.js",
                "path": "/home/adhil/Desktop/test/a/src/App.js",
                "content": "console.log(\"Hello world\")\n"
              }
            ],
            "builder": {
              "name": "package.json",
              "path": "/home/adhil/Desktop/test/a/package.json",
              "content": "{\n  \"name\": \"a\",\n  \"version\": \"1.0.0\",\n  \"main\": \"index.js\",\n  \"scripts\": {\n    \"test\": \"echo \\\"Error: no test specified\\\" && exit 1\"\n  },\n  \"keywords\": [],\n  \"author\": \"\",\n  \"license\": \"ISC\",\n  \"description\": \"\"\n}\n"
              }
            ],
            "framework": "NODE_JS",
            "projectName": "a"
          },
          "prompt": "make the project start with command 'npm start'"
        }
        ```

        ### Output
        Based on the provided context and prompt, the tool generates a shell script. This script can be executed to apply the requested changes or updates to the project.

        #### Example Response:
        ```sh
        #!/bin/bash
        echo "Setting up the project to start with 'npm start'"
        if ! grep -q "\"start\"" /home/adhil/Desktop/test/a/package.json; then
          sed -i 's/"scripts": {/&\n    \"start\": \"node src\/App.js\",/' /home/adhil/Desktop/test/a/package.json
        fi
        echo "Run 'npm start' to launch your project."
        ```

        ### Notes:
        - Ensure that the `context` object accurately represents the project files and structure.
        - The `prompt` should clearly specify the desired outcome.
        - The generated script assumes that required dependencies and tools are already installed.

    """.trimIndent()

}