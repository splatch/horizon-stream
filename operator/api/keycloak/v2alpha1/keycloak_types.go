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
	Instances             int                   `json:"instances"`
	DisableDefaultIngress bool                  `json:"disableDefaultIngress"`
	Hostname              string                `json:"hostname"`
	TlsSecret             string                `json:"tlsSecret"`
	Image                 string                `json:"image"`
	ServerConfiguration   []ServerConfiguration `json:"serverConfiguration"`
}

// +kubebuilder:object:generate=true

type ServerConfiguration struct {
	Name   string        `json:"name"`
	Value  string        `json:"value"`
	Secret *SecretConfig `json:"secret,omitempty"`
}

// +kubebuilder:object:generate=true

type SecretConfig struct {
	Name string `json:"name,omitempty"`
	Key  string `json:"key,omitempty"`
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

// +kubebuilder:object:generate=true

type KeycloakRealmImportSpec struct {
	KeycloakCRName string `json:"keycloakCRName"`
	Realm          Realm  `json:"realm"`
}

// +kubebuilder:object:generate=true

type Realm struct {
	AccessTokenLifeSpan  int        `json:"accessTokenLifeSpan"`
	Id                   string     `json:"id"`
	Realm                string     `json:"realm"`
	Enabled              bool       `json:"enabled"`
	LoginTheme           string     `json:"loginTheme"`
	EmailTheme           string     `json:"emailTheme"`
	RememberMe           bool       `json:"rememberMe"`
	ResetPasswordAllowed bool       `json:"resetPasswordAllowed"`
	Attributes           Attributes `json:"attributes"`
	Clients              []Client   `json:"clients"`
	Roles                Roles      `json:"roles"`
	Users                []User     `json:"users"`
}

// +kubebuilder:object:generate=true

type Attributes struct {
	FrontendURL string `json:"frontendURL"`
}

// +kubebuilder:object:generate=true

type Client struct {
	Id                        string   `json:"id"`
	ClientId                  string   `json:"clientId"`
	StandardFlowEnabled       bool     `json:"standardFlowEnabled"`
	Enabled                   bool     `json:"enabled"`
	WebOrigins                []string `json:"webOrigins"`
	RedirectUris              []string `json:"redirectUris"`
	PublicClient              bool     `json:"publicClient"`
	DirectAccessGrantsEnabled bool     `json:"directAccessGrantsEnabled"`
	DefaultClientScopes       []string `json:"defaultClientScopes"`
	OptionalClientScopes      []string `json:"optionalClientScopes"`
}

// +kubebuilder:object:generate=true

type Roles struct {
	Realm []RealmRole `json:"realm"`
}

type RealmRole struct {
	Id        string `json:"id"`
	Name      string `json:"name"`
	Composite bool   `json:"composite"`
}

// +kubebuilder:object:generate=true

type User struct {
	Username      string       `json:"username"`
	Email         string       `json:"email"`
	Enabled       bool         `json:"enabled"`
	EmailVerified bool         `json:"emailVerified"`
	Credentials   []Credential `json:"credentials"`
	RealmRoles    []string     `json:"realmRoles"`
	ClientRoles   ClientRole   `json:"clientRoles"`
}

type Credential struct {
	Type  string `json:"type"`
	Value string `json:"value"`
}

type SecretCred struct {
	Name string `json:"name"`
	Key  string `json:"key"`
}

// +kubebuilder:object:generate=true

type ClientRole struct {
	Account         []string `json:"account"`
	RealmManagement []string `json:"realm-management"`
}

// +kubebuilder:object:generate=true

type KeycloakRealmImportStatus struct {
	// todo?
}

// +kubebuilder:object:root=true
// +kubebuilder:subresource:status

// KeycloakRealmImport - is the Schema for KeycloakRealmImport
type KeycloakRealmImport struct {
	metav1.TypeMeta   `json:",inline"`
	metav1.ObjectMeta `json:"metadata,omitempty"`

	Spec   KeycloakRealmImportSpec   `json:"spec,omitempty"`
	Status KeycloakRealmImportStatus `json:"status,omitempty"`
}

// +kubebuilder:object:root=true

// KeycloakRealmImportList - contains a list of KeycloakRealmImportList
type KeycloakRealmImportList struct {
	metav1.TypeMeta `json:",inline"`
	metav1.ListMeta `json:"metadata,omitempty"`
	Items           []Keycloak `json:"items"`
}

func init() {
	SchemeBuilder.Register(&Keycloak{}, &KeycloakList{}, &KeycloakRealmImport{}, &KeycloakRealmImportList{})
}
