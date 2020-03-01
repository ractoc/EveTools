./stopAll.sh

sudo cp ./angular-${project.version}-executable.jar /opt/eve/angular.jar
sudo cp ./assets-${project.version}-executable.jar /opt/eve/assets.jar
sudo cp ./calculator-${project.version}-executable.jar /opt/eve/calculator.jar
sudo cp ./universe-${project.version}-executable.jar /opt/eve/universe.jar
sudo cp ./user-${project.version}-executable.jar /opt/eve/user.jar
sudo cp ./crawler-${project.version}-executable.jar /opt/eve/crawler.jar

sudo chown eve:eve /opt/eve/*.jar

sudo cp angular-app.service /etc/systemd/system/
sudo cp assets-app.service /etc/systemd/system/
sudo cp calculator-app.service /etc/systemd/system/
sudo cp universe-app.service /etc/systemd/system/
sudo cp user-app.service /etc/systemd/system/

sudo chmod u+rwx /etc/systemd/system/angular-app.service
sudo chmod u+rwx /etc/systemd/system/assets-app.service
sudo chmod u+rwx /etc/systemd/system/calculator-app.service
sudo chmod u+rwx /etc/systemd/system/universe-app.service
sudo chmod u+rwx /etc/systemd/system/user-app.service

sudo systemctl daemon-reload

sudo systemctl enable angular-app
sudo systemctl enable assets-app
sudo systemctl enable calculator-app
sudo systemctl enable universe-app
sudo systemctl enable user-app

./startAll.sh

