echo stopping all services

sudo systemctl stop angular-app
sudo systemctl stop assets-app
sudo systemctl stop fleetmanager-app
sudo systemctl stop universe-app
sudo systemctl stop user-app

echo removing previously installed version

sudo rm -rf /opt/eve/angular.jar
sudo rm -rf /opt/eve/assets.jar
sudo rm -rf /opt/eve/fleetmanager.jar
sudo rm -rf /opt/eve/universe.jar
sudo rm -rf /opt/eve/user.jar
sudo rm -rf /opt/eve/crawler.jar

echo installing new version

sudo cp /tmp/eve/angular-${project.version}-executable.jar /opt/eve/angular.jar
sudo cp /tmp/eve/assets-${project.version}-executable.jar /opt/eve/assets.jar
sudo cp /tmp/eve/fleetmanager-${project.version}-executable.jar /opt/eve/fleetmanager.jar
sudo cp /tmp/eve/universe-${project.version}-executable.jar /opt/eve/universe.jar
sudo cp /tmp/eve/user-${project.version}-executable.jar /opt/eve/user.jar
sudo cp /tmp/eve/crawler-${project.version}-executable.jar /opt/eve/crawler.jar

echo setting grants on executables
sudo chown eve:eve /opt/eve/*.jar

echo setting up services
sudo cp /tmp/eve/angular-app.service /etc/systemd/system/
sudo cp /tmp/eve/assets-app.service /etc/systemd/system/
sudo cp /tmp/eve/fleetmanager-app.service /etc/systemd/system/
sudo cp /tmp/eve/universe-app.service /etc/systemd/system/
sudo cp /tmp/eve/user-app.service /etc/systemd/system/

echo setting grants on services
sudo chmod u+rwx /etc/systemd/system/angular-app.service
sudo chmod u+rwx /etc/systemd/system/assets-app.service
sudo chmod u+rwx /etc/systemd/system/fleetmanager-app.service
sudo chmod u+rwx /etc/systemd/system/universe-app.service
sudo chmod u+rwx /etc/systemd/system/user-app.service

echo reloading service configuration
sudo systemctl daemon-reload

echo enabling services to start on server boot
sudo systemctl enable angular-app
sudo systemctl enable assets-app
sudo systemctl enable fleetmanager-app
sudo systemctl enable universe-app
sudo systemctl enable user-app

echo starting all services

sudo systemctl start angular-app
sudo systemctl start assets-app
sudo systemctl start calculator-app
sudo systemctl start universe-app
sudo systemctl start user-app
