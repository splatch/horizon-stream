//go:build !ignore_autogenerated
// +build !ignore_autogenerated

// Code generated by controller-gen. DO NOT EDIT.

package v1alpha1

import (
	runtime "k8s.io/apimachinery/pkg/runtime"
)

// DeepCopyInto is an autogenerated deepcopy function, copying the receiver, writing into out. in must be non-nil.
func (in *OpenNMS) DeepCopyInto(out *OpenNMS) {
	*out = *in
	out.TypeMeta = in.TypeMeta
	in.ObjectMeta.DeepCopyInto(&out.ObjectMeta)
	out.Spec = in.Spec
	in.Status.DeepCopyInto(&out.Status)
}

// DeepCopy is an autogenerated deepcopy function, copying the receiver, creating a new OpenNMS.
func (in *OpenNMS) DeepCopy() *OpenNMS {
	if in == nil {
		return nil
	}
	out := new(OpenNMS)
	in.DeepCopyInto(out)
	return out
}

// DeepCopyObject is an autogenerated deepcopy function, copying the receiver, creating a new runtime.Object.
func (in *OpenNMS) DeepCopyObject() runtime.Object {
	if c := in.DeepCopy(); c != nil {
		return c
	}
	return nil
}

// DeepCopyInto is an autogenerated deepcopy function, copying the receiver, writing into out. in must be non-nil.
func (in *OpenNMSList) DeepCopyInto(out *OpenNMSList) {
	*out = *in
	out.TypeMeta = in.TypeMeta
	in.ListMeta.DeepCopyInto(&out.ListMeta)
	if in.Items != nil {
		in, out := &in.Items, &out.Items
		*out = make([]OpenNMS, len(*in))
		for i := range *in {
			(*in)[i].DeepCopyInto(&(*out)[i])
		}
	}
}

// DeepCopy is an autogenerated deepcopy function, copying the receiver, creating a new OpenNMSList.
func (in *OpenNMSList) DeepCopy() *OpenNMSList {
	if in == nil {
		return nil
	}
	out := new(OpenNMSList)
	in.DeepCopyInto(out)
	return out
}

// DeepCopyObject is an autogenerated deepcopy function, copying the receiver, creating a new runtime.Object.
func (in *OpenNMSList) DeepCopyObject() runtime.Object {
	if c := in.DeepCopy(); c != nil {
		return c
	}
	return nil
}

// DeepCopyInto is an autogenerated deepcopy function, copying the receiver, writing into out. in must be non-nil.
func (in *OpenNMSSpec) DeepCopyInto(out *OpenNMSSpec) {
	*out = *in
	out.Credentials = in.Credentials
	out.API = in.API
	out.UI = in.UI
	out.Minion = in.Minion
	out.MinionGateway = in.MinionGateway
	out.MinionSSLGateway = in.MinionSSLGateway
	out.Inventory = in.Inventory
	out.Alert = in.Alert
	out.Notification = in.Notification
	out.MetricsProcessor = in.MetricsProcessor
	out.Postgres = in.Postgres
	out.Events = in.Events
	out.Keycloak = in.Keycloak
	out.DataChoices = in.DataChoices
	out.Grafana = in.Grafana
	out.UpdateConfig = in.UpdateConfig
}

// DeepCopy is an autogenerated deepcopy function, copying the receiver, creating a new OpenNMSSpec.
func (in *OpenNMSSpec) DeepCopy() *OpenNMSSpec {
	if in == nil {
		return nil
	}
	out := new(OpenNMSSpec)
	in.DeepCopyInto(out)
	return out
}

// DeepCopyInto is an autogenerated deepcopy function, copying the receiver, writing into out. in must be non-nil.
func (in *OpenNMSStatus) DeepCopyInto(out *OpenNMSStatus) {
	*out = *in
	out.Update = in.Update
	in.Readiness.DeepCopyInto(&out.Readiness)
	if in.Nodes != nil {
		in, out := &in.Nodes, &out.Nodes
		*out = make([]string, len(*in))
		copy(*out, *in)
	}
}

// DeepCopy is an autogenerated deepcopy function, copying the receiver, creating a new OpenNMSStatus.
func (in *OpenNMSStatus) DeepCopy() *OpenNMSStatus {
	if in == nil {
		return nil
	}
	out := new(OpenNMSStatus)
	in.DeepCopyInto(out)
	return out
}

// DeepCopyInto is an autogenerated deepcopy function, copying the receiver, writing into out. in must be non-nil.
func (in *ReadinessStatus) DeepCopyInto(out *ReadinessStatus) {
	*out = *in
	if in.Services != nil {
		in, out := &in.Services, &out.Services
		*out = make([]ServiceStatus, len(*in))
		copy(*out, *in)
	}
}

// DeepCopy is an autogenerated deepcopy function, copying the receiver, creating a new ReadinessStatus.
func (in *ReadinessStatus) DeepCopy() *ReadinessStatus {
	if in == nil {
		return nil
	}
	out := new(ReadinessStatus)
	in.DeepCopyInto(out)
	return out
}

// DeepCopyInto is an autogenerated deepcopy function, copying the receiver, writing into out. in must be non-nil.
func (in *UpdateStatus) DeepCopyInto(out *UpdateStatus) {
	*out = *in
}

// DeepCopy is an autogenerated deepcopy function, copying the receiver, creating a new UpdateStatus.
func (in *UpdateStatus) DeepCopy() *UpdateStatus {
	if in == nil {
		return nil
	}
	out := new(UpdateStatus)
	in.DeepCopyInto(out)
	return out
}
