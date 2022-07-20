/*
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package v2alpha1

import metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"

//KEYCLOAK INSTANCE

// +kubebuilder:object:generate=true

// KeycloakSpec - defines the desired state of Keycloak
type KeycloakSpec struct {
	Instances    int                   `yaml:"instances"`
	Hostname     string                `yaml:"hostname"`
	TLSSecret    string                `yaml:"tlsSecret"`
	ServerConfig []ServerConfiguration `yaml:"serverConfiguration"`
}

type ServerConfiguration struct {
	Name   string       `yaml:"name"`
	Value  string       `yaml:"value"`
	Secret SecretConfig `yaml:"secret"`
}

type SecretConfig struct {
	Name string `yaml:"name"`
	Key  string `yaml:"key"`
}

// +kubebuilder:object:generate=true

// KeycloakStatus - status of the Keycloak instance
type KeycloakStatus struct {
	//todo?
}

// +kubebuilder:object:root=true
// +kubebuilder:subresource:status

// Keycloak - is the Schema for Keycloak
type Keycloak struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KeycloakSpec   `json:"spec,omitempty"`
	Status KeycloakStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true

// KeycloakList - contains a list of Keycloak
type KeycloakList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Keycloak `json:"items"`
}

// REALM IMPORT

type KeycloakRealmImportSpec struct {
	KeycloakCRName string `yaml:"keycloakCRName"`
	Realm          Realm  `yaml:"realm"`
}

type Realm struct {
	AccessTokenLifeSpan  int        `yaml:"accessTokenLifeSpan"`
	Id                   string     `yaml:"id"`
	Realm                string     `yaml:"realm"`
	Enabled              bool       `yaml:"enabled"`
	LoginTheme           string     `yaml:"loginTheme"`
	EmailTheme           string     `yaml:"emailTheme"`
	RememberMe           bool       `yaml:"rememberMe"`
	ResetPasswordAllowed bool       `yaml:"resetPasswordAllowed"`
	Attributes           Attributes `yaml:"attributes"`
	Clients              []Client   `yaml:"clients"`
	Roles                Roles      `yaml:"roles"`
	Users                []User     `yaml:"users"`
}

type Attributes struct {
	FrontendURL string `yaml:"frontendURL"`
}

type Client struct {
	Id                        string   `yaml:"id"`
	ClientId                  string   `yaml:"clientId"`
	StandardFlowEnabled       bool     `yaml:"standardFlowEnabled"`
	Enabled                   bool     `yaml:"enabled"`
	WebOrigins                []string `yaml:"webOrigins"`
	RedirectUris              []string `yaml:"redirectUris"`
	PublicClient              bool     `yaml:"publicClient"`
	DirectAccessGrantsEnabled bool     `yaml:"directAccessGrantsEnabled"`
	DefaultClientScopes       []string `yaml:"defaultClientScopes"`
	OptionalClientScopes      []string `yaml:"optionalClientScopes"`
}

type Roles struct {
	Realm []RealmRole `yaml:"realm"`
}

type RealmRole struct {
	Id        string `yaml:"id"`
	Name      string `yaml:"name"`
	Composite bool   `yaml:"composite"`
}

type User struct {
	Username      string       `yaml:"username"`
	Email         string       `yaml:"email"`
	Enabled       bool         `yaml:"enabled"`
	EmailVerified bool         `yaml:"emailVerified"`
	Credentials   []Credential `yaml:"credentials"`
	RealmRoles    []string     `yaml:"realmRoles"`
	ClientRoles   []ClientRole `yaml:"clientRoles"`
}

type Credential struct {
	Type   string     `yaml:"type"`
	Secret SecretCred `yaml:"secret"`
}

type SecretCred struct {
	Name string `yaml:"name"`
	Key  string `yaml:"key"`
}

type ClientRole struct {
	Account         []string `yaml:"account"`
	RealmManagement []string `yaml:"realmManagement"`
}

type KeycloakRealmImportStatus struct {
	// todo?
}

// KeycloakRealmImport - is the Schema for KeycloakRealmImport
type KeycloakRealmImport struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KeycloakRealmImportSpec   `json:"spec,omitempty"`
	Status KeycloakRealmImportStatus `json:"status,omitempty"`
}

// KeycloakRealmImportList - contains a list of KeycloakRealmImportList
type KeycloakRealmImportList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Keycloak `json:"items"`
}

func init() {
	SchemeBuilder.Register(&Keycloak{}, &KeycloakList{})
}
