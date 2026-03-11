export interface Event {
  id: number;
  title: string;
  description: string;
  location: string | null;
  startTime: string;
  endTime: string;
  owner: string;
  attendees: string[];
  createdAt: string;
}

export interface EventRequest {
  title: string;
  description?: string;
  location?: string;
  startTime: string;
  endTime: string;
  attendees?: string[];
}
