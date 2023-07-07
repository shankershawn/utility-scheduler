Please execute below docker run command to create container

docker stop utility-scheduler
docker rm utility-scheduler
docker pull shankershawn/utility-scheduler
docker run -tid -e "VAULT_TOKEN=<vault_token>" --name=utility-scheduler -p 8080:8080 shankershawn/utility-scheduler
