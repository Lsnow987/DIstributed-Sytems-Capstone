from fastapi import FastAPI
from pydantic import BaseModel
from subprocess import Popen, PIPE

app = FastAPI()

class Command(BaseModel):
    command: str

@app.post("/scale/")
async def scale_command(cmd: Command):
    try:
        print("Received command:", cmd.command)
        process = Popen(cmd.command, shell=True, stdout=PIPE, stderr=PIPE)
        stdout, stderr = process.communicate()
        if process.returncode == 0:
            print("Received command and worked:", cmd.command)
            return {"success": True, "stdout": stdout.decode(), "stderr": stderr.decode()}
        else:
            print("Received command and failed:", cmd.command)
            print(stderr.decode())
            return {"success": False, "error": stderr.decode()}
    except Exception as e:
        return {"success": False, "error": str(e)}
