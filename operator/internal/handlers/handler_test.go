//go:build unit

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

package handlers

import (
	"github.com/OpenNMS/opennms-operator/config"
	"github.com/OpenNMS/opennms-operator/internal/model/values"
	values2 "github.com/OpenNMS/opennms-operator/internal/util/values"
	"github.com/stretchr/testify/assert"
	v1 "k8s.io/api/apps/v1"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"testing"
)

func TestFilepath(t *testing.T) {
	res := filepath("test")
	assert.Equal(t, ConfigFilePath+"test", res)
}

func TestServiceHandler(t *testing.T) {
	sh := ServiceHandlerObject{}
	obj := v1.Deployment{}
	obj.SetName("test")
	sh.Config = []client.Object{
		&obj,
	}

	res := sh.GetConfig()
	assert.NotNil(t, res)
	assert.Equal(t, "test", res[0].GetName(), "should return the config that was set on the handler")

	sh.SetDeployed(true)
	resB := sh.GetDeployed()
	assert.True(t, resB, "should return the deployed status correctly")

	sh.AddToTemplates("filename", values.TemplateValues{}, &v1.Deployment{})
	resC := sh.GetTemplates()
	assert.Equal(t, "filename", resC[0].Filename, "should add and get templates correctly")

}

var valuesSet bool

var tvals values.TemplateValues

func DefaultValues() values.TemplateValues {
	if !valuesSet {
		tvals, _ = values2.GetDefaultValues(config.OperatorConfig{
			DefaultOpenNMSValuesFile: "./../../charts/opennms/values.yaml",
		})
		valuesSet = true
	}
	return tvals
}
