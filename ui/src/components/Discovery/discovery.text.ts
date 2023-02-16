export default {
  Discovery: {
    pageHeadline: 'Discovery',
    headline1: 'Select a discovery',
    headline2: 'ICMP/SNMP Discovery Setup',
    nameInputLabel: 'ICMP/SNMP name',
    noneDiscoverySelectedMsg: 'Select a discovery to get started',
    button: {
      add: 'New Discovery',
      cancel: 'Cancel',
      submit: 'Save discovery'
    }
  },
  AddDiscoverySection: {
    activeDiscoveryTitle: 'Active Discovery',
    passiveDiscoveryTitle: 'Passive Discovery',
    icmpSnmp: 'ICMP/SNMP',
    azure: 'AZURE',
    syslog: 'Syslog & SNMP Traps'
  },
  ContentEditable: {
    IP: {
      label: 'Enter IP ranges and/or subnets'
    },
    CommunityString: {
      label: 'Enter community string (optional)'
    },
    UDPPort: {
      label: 'Enter UDP port (optional)'
    }
  }
}

export const Azure = {
  title:'Azure Discovery Setup',
  saveBtnText: 'Save Discovery',
  cancelBtnText: 'Cancel'
}

export const SuccessModalOptions = {
  successMsg: 'setup successfully!',
  title: 'You may be interested in...',
  viewNodes: 'View Detected Nodes',
  addDiscovery:'Add Another Discovery',
  addTransaction: 'Add Synthetic Transaction',
  addMonitoring: 'Add Monitoring Policy',
  checkboxText: 'Don\'t show this again'
}
