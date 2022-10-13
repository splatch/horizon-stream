## Tilt config ##
load('ext://tilt_inspector', 'tilt_inspector')
tilt_inspector()

load('ext://k8s_attach', 'k8s_attach')
# load('ext://namespace', 'namespace_yaml', 'namespace_inject')

secret_settings(disable_scrub=True)  ## TODO: update secret values so we can reenable scrub
update_settings(suppress_unused_image_warnings=["opennms/horizon-stream-grafana", "opennms/horizon-stream-keycloak"])

## Namespace ##
# k8s_yaml(namespace_yaml('tilt-instance'), allow_duplicates=True)
# k8s_yaml(namespace_yaml('tilt-operator'), allow_duplicates=True)

## Operator ##
# local_resource('chart-deps', cmd='cd charts/opennms-operator && helm dep update')
# k8s_yaml(helm('./charts/opennms-operator',
#               namespace='tilt-operator',
#               values=['./operator/values.yaml'], ))
#
# docker_build("opennms/operator", "operator")

## OpenNMS ##
# k8s_yaml(helm('./charts-skaffold/opennms',
#               namespace='tilt-instance',
#               set=[
#                   'Namespace=tilt-instance',
#                   'OpenNMS.Core.ImagePullPolicy=Never',
#                   'OpenNMS.API.ImagePullPolicy=Never',
#                   'OpenNMS.UI.ImagePullPolicy=Never',
#                   'Grafana.Image=hendrikmaus/kubernetes-dummy-image',
#                   'Grafana.ImagePullPolicy=IfNotPresent',
#               ]))
#
# k8s_kind('OpenNMS',
#          api_version='opennms.k8s.opennms.com/v1alpha1',
#          image_json_path=['{.spec.ui.image}', '{.spec.core.image}', '{.spec.api.image}'],
#          pod_readiness='ignore')
# k8s_yaml(read_file('./operator/local-instance-tilt.yaml'))
# k8s_resource('local-instance',
#              new_name='opennms-crd', )


## Helm ##
k8s_yaml(
    helm('charts/opennms',
         values=['./skaffold-helm-values.yaml'],
         # set=['Keycloak.Image=opennms/horizon-stream-keycloak-dev',
         #      'Grafana.Image=opennms/horizon-stream-grafana-dev'
         # ],
    )
)

## Shared Library ##
local_resource('shared-lib',
               cmd='mvn clean install -DskipTests=true',
               dir='shared-lib',
               deps=['./shared-lib'],
               ignore=['**/target/']
               )

### Core ###
# local_resource(
#     'compile-core',
#     'mvn install -f platform/alarms/daemon',
#     deps=['./platform/alarms/daemon'],
#     ignore=['**/target'], )
custom_build(
    'opennms/horizon-stream-core',
    'mvn install -Pbuild-docker-images-enabled -DskipTests -Ddocker.image=$EXPECTED_REF -f platform',
    deps=['./platform'],
    ignore=['**/target', '**/dependency-reduced-pom.xml'],
    # live_update=[
    #     sync('./platform/*.jar', '/opt/horizon-stream/deploy'),
    # ],
)

k8s_resource('opennms-core',
             port_forwards=['11022:8101', '11080:8181', '11050:5005'], )

### Minion ###
# local_resource(
#     'compile-core',
#     'mvn install -f platform/alarms/daemon',
#     deps=['./platform/alarms/daemon'],
#     ignore=['**/target'], )
custom_build(
    'opennms/horizon-stream-minion',
    'mvn install -f minion -Ddocker.image=$EXPECTED_REF -Dtest=false -DfailIfNoTests=false -DskipITs=true -DskipTests=true',
    deps=['./minion'],
    ignore=['**/target', '**/dependency-reduced-pom.xml'],
    # live_update=[
    #     sync('./platform/*.jar', '/opt/horizon-stream/deploy'),
    # ],
)

k8s_resource('opennms-minion',
             port_forwards=['12022:8101', '12080:8181', '12050:5005'], )

### minion gateway ###
local_resource(
    'compile-minion-gateway-parent-pom',
    'mvn compile -f minion-gateway -N',
    deps=['./minion-gateway/pom.xml'],
    ignore=['**/target'],
)
local_resource(
    'compile-ignite-detector',
    'mvn compile -f minion-gateway -pl ignite-detector',
    deps=['./minion-gateway/ignite-detector'],
    ignore=['**/target'],
    resource_deps=[
        'compile-minion-gateway-parent-pom',
    ]
)
local_resource(
    'compile-minion-gateway',
    'mvn compile -f minion-gateway -pl main -am',
    deps=['./minion-gateway/main/src', './minion-gateway/main/pom.xml'],
    ignore=['**/target'],
    resource_deps=[
        'compile-minion-gateway-parent-pom',
        'compile-ignite-detector',
   ]
)

custom_build(
    'opennms/horizon-stream-minion-gateway',
    'mvn jib:dockerBuild -Dimage=$EXPECTED_REF -f minion-gateway -pl main',
    deps=['./minion-gateway/main/target/classes', './minion-gateway/main/pom.xml'],
    live_update=[
        sync('./minion-gateway/main/target/classes/java/main', '/app/classes'),
    ], )

k8s_resource('opennms-minion-gateway',
             port_forwards=['9090', '13050:5005'],
             resource_deps=['compile-minion-gateway'],
)

### rest server ###
local_resource(
    'compile-rest-server',
    'mvn compile -f rest-server',
    deps=['./rest-server/src', './rest-server/pom.xml'],
    ignore=['**/target'],
)

custom_build(
    'opennms/horizon-stream-rest-server',
    'mvn jib:dockerBuild -Dimage=$EXPECTED_REF -f rest-server',
    deps=['./rest-server/target/classes', './rest-server/pom.xml'],
    live_update=[
        sync('./rest-server/target/classes/java/main', '/app/classes'),
    ], )

k8s_resource('opennms-rest-server',
             port_forwards=['9090', '13050:5005'],
             resource_deps=['compile-rest-server'], )

### notification ###
local_resource(
    'compile-notifications',
    'mvn compile -f notifications',
    deps=['./notifications/src', './notifications/pom.xml'],
    ignore=['**/target'],
)

custom_build(
    'opennms/horizon-stream-notification',
    'mvn jib:dockerBuild -Dimage=$EXPECTED_REF -f notifications',
    deps=['./notifications/target/classes', './notifications/pom.xml'],
    live_update=[
        sync('./notifications/target/classes/java/main', '/app/classes'),
    ],
)

k8s_resource('opennms-notifications',
             port_forwards=['15080:8080', '15050:5005'],
             resource_deps=['compile-notifications'],
)

### ui ###
docker_build("opennms/horizon-stream-ui",
             "ui",
             target="development",
             live_update=[
                 sync('./ui', '/app'),
                 run('yarn install', trigger=['./ui/package.json', './ui/yarn.lock']),
             ],
)
k8s_resource('opennms-ui',
             port_forwards=['17080:80'],
)

### keycloak ###
docker_build("opennms/horizon-stream-keycloak",
             "keycloak-ui",
             target="development",
             live_update=[
                 sync('./keycloak-ui/themes', '/opt/keycloak/themes')
             ],
)
k8s_resource('onms-keycloak')

### grafana ###
docker_build("opennms/horizon-stream-grafana",
             "grafana",
)
k8s_resource(
    'grafana',
    port_forwards=['18080:3000'],
)

### ingress ###
k8s_resource(
    'ingress-nginx-controller',
    port_forwards=['8123:80'],
)
