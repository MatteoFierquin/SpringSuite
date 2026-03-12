export interface EventInvitee {
  email: string;
  status: 'PENDING' | 'ACCEPTED' | 'DECLINED';
}

export interface Event {
  id: number;
  title: string;
  description: string;
  location: string | null;
  startTime: string;
  endTime: string;
  owner: string;
  attendees: string[];
  invitees: EventInvitee[];
  createdAt: string;
}

export interface EventRequest {
  title: string;
  description?: string;
  location?: string;
  startTime: string;
  endTime: string;
  attendees?: string[];
  invitees?: EventInvitee[];
}

export interface InviteeRequest {
  email: string;
  status?: 'PENDING' | 'ACCEPTED' | 'DECLINED';
}
