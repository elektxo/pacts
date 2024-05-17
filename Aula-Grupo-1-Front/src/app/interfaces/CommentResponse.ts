export interface CommentResponse {
    id:number;
    text:string;
    creatorUsername:string;
    createdAt:Date;
    updatedAt?:Date;
}