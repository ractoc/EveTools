echo stopping all services

sudo systemctl stop eve-tools-app.service

echo removing previously installed version

sudo rm -rf /var/www/html/*
sudo rm -rf /opt/eve/fleetmanager.jar
sudo rm -rf /opt/eve/user.jar

echo installing new version

sudo tar -C /var/www/html -zxvf /tmp/eve/angular-1.0-SNAPSHOT.tar.gz
sudo chmod -R 775 /var/www/html
sudo chown -R root:www-data /var/www/html

sudo cp /tmp/eve/fleetmanager-${project.version}-executable.jar /opt/eve/fleetmanager.jar
sudo cp /tmp/eve/user-${project.version}-executable.jar /opt/eve/user.jar
sudo cp /tmp/eve/runAll.sh /opt/eve/runAll.sh

echo setting grants on executables
sudo chown eve:eve /opt/eve/*.jar
sudo chown eve:eve /opt/eve/runAll.sh
sudo chmod u+rwx /opt/eve/runAll.sh

echo setting up services
sudo cp /tmp/eve/eve-tools-app.service /etc/systemd/system/

echo setting grants on services
sudo chmod u+rwx /etc/systemd/system/eve-tools-app.service

echo reloading service configuration
sudo systemctl daemon-reload

echo enabling services to start on server boot
sudo systemctl enable eve-tools-app.service

echo starting all services

sudo systemctl start eve-tools-app.service
