import { User } from './user.model';
import { Subject } from './subject.model';

export interface Subscription {
    id: number;
    user: User;
    subject: Subject;
    dateCreation: string;
} 