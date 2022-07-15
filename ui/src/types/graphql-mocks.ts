import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};

export type Device = {
  __typename?: 'Device';
  icmp_latency: Scalars['String'];
  id: Scalars['ID'];
  name: Scalars['String'];
  snmp_uptime: Scalars['String'];
};

export type ListDevices = {
  __typename?: 'ListDevices';
  count: Scalars['String'];
  devices: Array<Device>;
  offset: Scalars['String'];
  totalCount: Scalars['String'];
};

export type Mutation = {
  __typename?: 'Mutation';
  saveRoutingKey?: Maybe<Scalars['String']>;
};


export type MutationSaveRoutingKeyArgs = {
  key: Scalars['String'];
};

export type Query = {
  __typename?: 'Query';
  device: Device;
  listDevices: ListDevices;
};

export type SaveRoutingKeyMutationVariables = Exact<{
  key: Scalars['String'];
}>;


export type SaveRoutingKeyMutation = { __typename?: 'Mutation', saveRoutingKey?: string | null };

export type ListDevicesQueryVariables = Exact<{ [key: string]: never; }>;


export type ListDevicesQuery = { __typename?: 'Query', listDevices: { __typename?: 'ListDevices', count: string, totalCount: string, offset: string, devices: Array<{ __typename?: 'Device', id: string, name: string, icmp_latency: string, snmp_uptime: string }> } };


export const SaveRoutingKeyDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"SaveRoutingKey"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"key"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"String"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"saveRoutingKey"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"key"},"value":{"kind":"Variable","name":{"kind":"Name","value":"key"}}}]}]}}]} as unknown as DocumentNode<SaveRoutingKeyMutation, SaveRoutingKeyMutationVariables>;
export const ListDevicesDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"ListDevices"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"listDevices"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"devices"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"icmp_latency"}},{"kind":"Field","name":{"kind":"Name","value":"snmp_uptime"}}]}},{"kind":"Field","name":{"kind":"Name","value":"count"}},{"kind":"Field","name":{"kind":"Name","value":"totalCount"}},{"kind":"Field","name":{"kind":"Name","value":"offset"}}]}}]}}]} as unknown as DocumentNode<ListDevicesQuery, ListDevicesQueryVariables>;